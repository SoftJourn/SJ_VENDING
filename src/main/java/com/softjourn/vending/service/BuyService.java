package com.softjourn.vending.service;


import com.softjourn.vending.dto.Position;
import com.softjourn.vending.dto.ProductDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.exceptions.NotFoundException;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@Setter
public class BuyService {

    private VendingService vendingService;
    private MachineService machineService;
    private CoinService coinService;
    private FieldService fieldService;

    @Autowired
    public BuyService(VendingService vendingService, MachineService machineService, CoinService coinService, FieldService fieldService) {
        this.vendingService = vendingService;
        this.machineService = machineService;
        this.coinService = coinService;
        this.fieldService = fieldService;
    }

    public List<ProductDTO> getAvailableProducts(Integer machineId) {
        VendingMachine vendingMachine = vendingService.get(machineId);

        List<ProductDTO> result = new ArrayList<>();

        List<Row> rows = vendingMachine.getRows();
        for (int i = 0; i < rows.size(); i++) {
            List<Field> fields = rows.get(i).getFields();
            for (int j = 0; j < fields.size(); j++) {
                Field field = fields.get(j);
                if(field.getProduct() != null && field.getCount() > 0) {
                    Position position = new Position(i, j, field.getInternalId());
                    ProductDTO dto = new ProductDTO(field.getProduct(), position);
                    result.add(dto);
                }
            }
        }

        return result;
    }

    public boolean buy(Integer machineId, Integer productId, Principal principal) {
        return buy(machineId, getFieldByProduct(machineId, productId), principal);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public synchronized boolean buy(Integer machineId, String itemId, Principal principal) {
        Product product = getProductIfAvailable(machineId, itemId);
        coinService.spent(principal, product.getPrice());
        machineService.bye(machineId, itemId);
        decreaseProductsCount(machineId, itemId);
        return true;
    }

    private String getFieldByProduct(Integer machineId, Integer productId) {
        return vendingService.get(machineId)
                .getRows().stream()
                .flatMap(row -> row.getFields().stream())
                .filter(field -> field.getProduct() != null && field.getProduct().getId().equals(productId))
                .findFirst()
                .map(Field::getInternalId)
                .orElse(null);
    }

    private Product getProductIfAvailable(Integer machineId, String fieldInternalId) {
        Field field = getByInternalId(machineId, fieldInternalId);
        if(field.getCount() > 0 && field.getProduct() != null) {
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
        field.setCount(field.getCount());
        fieldService.update(field.getId(), field, machineId);
    }

}
