package com.softjourn.vending.service;


import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.dao.PurchaseRepository;
import com.softjourn.vending.dto.CategoryDTO;
import com.softjourn.vending.dto.FeatureDTO;
import com.softjourn.vending.dto.PurchaseProductDto;
import com.softjourn.vending.dto.TransactionDTO;
import com.softjourn.vending.entity.*;
import com.softjourn.vending.exceptions.MachineBusyException;
import com.softjourn.vending.exceptions.NotFoundException;
import com.softjourn.vending.exceptions.ProductNotFoundInMachineException;
import com.softjourn.vending.exceptions.VendingProcessingException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Setter
@Slf4j
public class BuyService {

    private static final long BES_SELLERS_LIMIT = 10;
    private static final long NEW_PRODUCTS_LIMIT = 10;
    private static final long LAST_PURCHASES_LIMIT = 10;


    private VendingService vendingService;
    private MachineService machineService;
    private PurchaseRepository purchaseRepository;
    private CoinService coinService;
    private FieldService fieldService;
    private CategoriesService categoriesService;
    private ProductRepository productRepository;
    private ReentrantLock lock = new ReentrantLock();

    @Autowired
    public BuyService(VendingService vendingService,
                      MachineService machineService,
                      PurchaseRepository purchaseRepository,
                      CoinService coinService,
                      FieldService fieldService,
                      CategoriesService categoriesService,
                      ProductRepository productRepository) {
        this.vendingService = vendingService;
        this.machineService = machineService;
        this.purchaseRepository = purchaseRepository;
        this.coinService = coinService;
        this.fieldService = fieldService;
        this.categoriesService = categoriesService;
        this.productRepository = productRepository;
    }

    public List<Product> getAvailableProducts(Integer machineId) {
        VendingMachine vendingMachine = vendingService.get(machineId);
        List<Row> rows = vendingMachine.getRows();
        return rows.stream()
                .flatMap(r -> r.getFields().stream())
                .filter(f -> f.getCount() > 0)
                .map(Field::getProduct)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public BigDecimal buy(Integer machineId, String itemId, Principal principal) {
        return buyIfNotLocked(machineId, itemId, principal, this::buyProduct);
    }

    @Transactional
    public BigDecimal buy(Integer machineId, Integer productId, Principal principal) {
        return buyIfNotLocked(machineId, getFieldByProduct(machineId, productId), principal, this::buyProduct);
    }

    private BigDecimal buyProduct(Integer machineId, String itemId, Principal principal) {
        Product product = getProductIfAvailable(machineId, itemId);
        TransactionDTO tx = null;
        try {
            VendingMachine machine = vendingService.get(machineId);
            tx = coinService.spent(principal, product.getPrice(), machine.getUniqueId());
            machineService.buy(machineId, itemId);
            decreaseProductsCount(machineId, itemId);
            savePurchase(machineId, product, principal);
            return tx.getRemain();
        } catch (VendingProcessingException ve) {
            if (tx == null || tx.getId() == null) {
                log.warn("Unsuccessful vending but money can't be returned. " + tx, ve);
            } else {
                coinService.returnMoney(tx);
            }
            throw ve;
        }
    }

    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    private BigDecimal buyIfNotLocked(Integer machineId,
                                      String itemId,
                                      Principal principal,
                                      TriFunction<Integer, String, Principal, BigDecimal> function) {
        if (lock.tryLock()) {
            try {
                return function.apply(machineId, itemId, principal);
            } finally {
                lock.unlock();
            }
        } else {
            throw new MachineBusyException(machineId);
        }
    }

    public List<Integer> getBestSellers(Integer machineId) {
        return purchaseRepository.getAllByMachineId(machineId).stream()
                .map(Purchase::getProductName)
                .map(productRepository::getProductByName)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Comparator.<Map.Entry<Product, Long>, Long>comparing(Map.Entry::getValue).reversed())
                .limit(BES_SELLERS_LIMIT)
                .map(Map.Entry::getKey)
                .map(Product::getId)
                .collect(Collectors.toList());
    }

    public List<Integer> getLastAdded(Integer machineId) {
        return vendingService.get(machineId)
                .getFields().stream()
                .filter(field -> field.getLoaded() != null)
                .sorted(Comparator.comparing(Field::getLoaded).reversed())
                .map(Field::getProduct)
                .filter(Objects::nonNull)
                .map(Product::getId)
                .distinct()
                .limit(NEW_PRODUCTS_LIMIT)
                .collect(Collectors.toList());
    }

    public List<PurchaseProductDto> lastPurchases(Principal principal) {
        return purchaseRepository.getAllByUser(principal.getName()).stream()
                .sorted(Comparator.comparing(Purchase::getTime).reversed())
                .map(p -> new PurchaseProductDto(p.getProductName(), p.getProductPrice(), p.getTime()))
                .limit(LAST_PURCHASES_LIMIT)
                .collect(Collectors.toList());
    }

    public List<Product> getByCategory(Category category, Integer machineId) {
        return getAvailableProducts(machineId).stream()
                .filter(p -> p.getCategory().getName().equals(category.getName()))
                .collect(Collectors.toList());
    }

    public List<Product> getByCategoryName(String categoryName, Integer machineId) {
        Category category = categoriesService.getByName(categoryName);

        return getByCategory(category, machineId);
    }

    public FeatureDTO getFeatures(Integer machineId) {
        List<Integer> lastAdded = getLastAdded(machineId);
        List<Integer> bestSellers = getBestSellers(machineId);
        List<CategoryDTO> categories = categoriesService.getAll().stream()
                .map(category -> new CategoryDTO(category.getName(), getByCategory(category, machineId)))
                .collect(Collectors.toList());
        return new FeatureDTO(lastAdded, bestSellers, categories);
    }

    private Stream<Product> getAvailableProductsStream(Integer machineId) {
        VendingMachine machine = vendingService.get(machineId);
        if (machine == null) throw new NotFoundException("There is no machine with id " + machineId);
        return machine.getFields().stream()
                .filter(f -> f.getProduct() != null && f.getCount() > 0)
                .map(Field::getProduct);
    }

    private void savePurchase(Integer machineId, Product product, Principal principal) {
        Purchase purchase = Purchase.builder()
                .account(principal.getName())
                .productName(product.getName())
                .productPrice(product.getPrice())
                .machine(vendingService.get(machineId))
                .time(Instant.now())
                .build();
        purchaseRepository.save(purchase);
    }

    private String getFieldByProduct(Integer machineId, Integer productId) {
        return vendingService.get(machineId)
                .getRows().stream()
                .flatMap(row -> row.getFields().stream())
                .filter(field -> field.getProduct() != null && field.getProduct().getId().equals(productId))
                .filter(field -> field.getCount() > 0)
                .findFirst()
                .map(Field::getInternalId)
                .orElseThrow(() -> new ProductNotFoundInMachineException(String.format(
                        "Product not found in machine with id %d",
                        machineId))
                );
    }

    private Product getProductIfAvailable(Integer machineId, String fieldInternalId) {
        Field field = getByInternalId(machineId, fieldInternalId);
        if (field.getCount() > 0 && field.getProduct() != null) {
            return field.getProduct();
        } else {
            throw new ProductNotFoundInMachineException(String.format("Product not found in machine with id %d", machineId));
        }
    }

    private Field getByInternalId(Integer machineId, String fieldInternalId) {
        return vendingService.get(machineId).getFields()
                .stream()
                .filter(field -> field.getInternalId().equalsIgnoreCase(fieldInternalId))
                .findAny()
                .orElseThrow(() -> new NotFoundException("There is no such field in the machine."));
    }

    private void decreaseProductsCount(Integer machineId, String fieldInternalId) {
        Field field = getByInternalId(machineId, fieldInternalId);
        field.setCount(field.getCount() - 1);
        if (field.getCount() == 0) {
            field.setProduct(null);
        }
        fieldService.update(field.getId(), field, machineId);
    }

}
