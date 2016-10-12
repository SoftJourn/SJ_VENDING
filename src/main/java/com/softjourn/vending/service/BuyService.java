package com.softjourn.vending.service;


import com.softjourn.vending.dao.PurchaseRepository;
import com.softjourn.vending.dto.CategoryDTO;
import com.softjourn.vending.dto.FeatureDTO;
import com.softjourn.vending.dto.PurchaseProductDto;
import com.softjourn.vending.entity.*;
import com.softjourn.vending.exceptions.NotFoundException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Setter
public class BuyService {

    private static final long BES_SELLERS_LIMIT = 10;
    private static final long NEW_PRODUCTS_LIMIT = 10;
    private static final long LAST_PURCHASES_LIMIT = 10;


    private VendingService vendingService;
    private MachineService machineService;
    private PurchaseRepository purchaseRepository;
    private CoinService coinService;
    private FieldService fieldService;

    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    public BuyService(VendingService vendingService, MachineService machineService, PurchaseRepository purchaseRepository, CoinService coinService, FieldService fieldService) {
        this.vendingService = vendingService;
        this.machineService = machineService;
        this.purchaseRepository = purchaseRepository;
        this.coinService = coinService;
        this.fieldService = fieldService;
    }

    public List<Product> getAvailableProducts(Integer machineId) {
        VendingMachine vendingMachine = vendingService.get(machineId);
        List<Product> result = new ArrayList<>();
        List<Row> rows = vendingMachine.getRows();

        for (int i = 0; i < rows.size(); i++) {
            List<Field> fields = rows.get(i).getFields();
            for (int j = 0; j < fields.size(); j++) {
                Field field = fields.get(j);
                if (field.getProduct() != null && field.getCount() > 0) {
                    result.add(field.getProduct());
                }
            }
        }
        return result;
    }

    public BigDecimal buy(Integer machineId, Integer productId, Principal principal) {
        return buy(machineId, getFieldByProduct(machineId, productId), principal);
    }

    @Transactional
    public synchronized BigDecimal buy(Integer machineId, String itemId, Principal principal) {
        Product product = getProductIfAvailable(machineId, itemId);
        VendingMachine machine = vendingService.get(machineId);
        BigDecimal remain = coinService.spent(principal, product.getPrice(), machine.getName());
        machineService.buy(machineId, itemId);
        decreaseProductsCount(machineId, itemId);
        savePurchase(machineId, product, principal);
        return remain;
    }

    public List<Product> getBestSellers(Integer machineId) {
        return purchaseRepository.getAllByMachineId(machineId).stream()
                .map(Purchase::getProduct)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(BES_SELLERS_LIMIT)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Product> getNew(Integer machineId) {
        return getAvailableProductsStream(machineId)
                .sorted((p1, p2) -> p2.getAddedTime().compareTo(p1.getAddedTime()))
                .distinct()
                .limit(NEW_PRODUCTS_LIMIT)
                .collect(Collectors.toList());
    }

    public List<PurchaseProductDto> lastPurchases(Principal principal) {
        return purchaseRepository.getAllByUser(principal.getName()).stream()
                .sorted((p1, p2) -> p2.getTime().compareTo(p1.getTime()))
                .map(p -> new PurchaseProductDto(p.getProduct(), p.getTime()))
                .limit(LAST_PURCHASES_LIMIT)
                .collect(Collectors.toList());
    }

    public List<Product> getByCategory(Categories categories, Integer machineId) {
        return getAvailableProducts(machineId).stream()
                .filter(p -> p.getCategory().getName().equals(categories.getName()))
                .collect(Collectors.toList());
    }

    public FeatureDTO getFeatures(Integer machineId) {
        FeatureDTO dto = new FeatureDTO();
        List<Integer> lastAdded = new ArrayList<>();
        getNew(machineId).forEach(product -> lastAdded.add(product.getId()));
        List<Integer> bestSellers = new ArrayList<>();
        getBestSellers(machineId).forEach(product -> bestSellers.add(product.getId()));
        dto.setLastAdded(lastAdded);
        dto.setBestSellers(bestSellers);
        List<CategoryDTO> categories = new ArrayList<>();
        categoriesService.getAll()
                .forEach(c -> {
                    categories.add(new CategoryDTO(c.getName(), getByCategory(c, machineId)));
                });
        dto.setCategories(categories);
        return dto;
    }

    private Stream<Product> getAvailableProductsStream(Integer machineId) {
        VendingMachine machine = vendingService.get(machineId);
        if (machine == null) throw new NotFoundException("There is no machine with id " + machineId);
        return machine.getFields().stream()
                .filter(f -> f.getProduct() != null && f.getCount() > 0)
                .map(Field::getProduct);
    }

    private void savePurchase(Integer machineId, Product product, Principal principal) {
        Purchase purchase = new Purchase();
        purchase.setAccount(principal.getName());
        purchase.setProduct(product);
        purchase.setTime(Instant.now());
        purchase.setMachine(vendingService.get(machineId));
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
                .orElse(null);
    }

    private Product getProductIfAvailable(Integer machineId, String fieldInternalId) {
        Field field = getByInternalId(machineId, fieldInternalId);
        if (field.getCount() > 0 && field.getProduct() != null) {
            return field.getProduct();
        } else {
            throw new NotFoundException("There is no products in this field.");
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
        fieldService.update(field.getId(), field, machineId);
    }

}
