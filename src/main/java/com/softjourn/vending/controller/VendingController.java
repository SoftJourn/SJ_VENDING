package com.softjourn.vending.controller;


import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.service.FieldService;
import com.softjourn.vending.service.VendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/v1/vending")
@PreAuthorize("hasAnyRole('INVENTORY','SUPER_ADMIN')")
public class VendingController {

    private VendingService vendingService;

    private FieldService fieldService;

    @Autowired
    public VendingController(VendingService vendingService,
                             FieldService fieldService) {
        this.vendingService = vendingService;
        this.fieldService = fieldService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<VendingMachine> getMachines() {
        return vendingService.getAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public VendingMachine getMachine(@PathVariable Integer id) {
        return vendingService.get(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public VendingMachine add(@RequestBody VendingMachineBuilderDTO machineBuilder, Principal principal) {
        return vendingService.create(machineBuilder, principal);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public VendingMachine update(@RequestBody VendingMachine machine) {
        return vendingService.update(machine);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public VendingMachine refill(@RequestBody VendingMachine machine, Principal principal) {
        return vendingService.refill(machine, principal);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer id) {
        vendingService.delete(id);
    }

    @RequestMapping(value = "/{id}/fields/{fieldId}", method = RequestMethod.POST)
    public Field updateField(@PathVariable Integer id,
                             @PathVariable Integer fieldId,
                             @RequestBody Field field) {
        return fieldService.update(fieldId, field, id);
    }

    @RequestMapping(value = "/{id}/rows/{rowId}", method = RequestMethod.POST)
    public Row updateRow(@PathVariable Integer id,
                         @PathVariable Integer rowId,
                         @RequestBody Integer count) {
        return fieldService.updateFieldsCountInRow(rowId, count);
    }

    @PreAuthorize("authenticated")
    @RequestMapping(value = "/price", method = RequestMethod.GET)
    public Map<String, BigDecimal> getAllMachinesLoadedPrice() {
        return Collections.singletonMap("amount", vendingService.getLoadedPrice());
    }

    @RequestMapping(value = "/{id}/price", method = RequestMethod.GET)
    public Map<String, BigDecimal> getMachineLoadedPrice(@PathVariable Integer id) {
        return Collections.singletonMap("amount", vendingService.getLoadedPrice(id));
    }

    @RequestMapping(value = "{id}/price/undistributed", method = RequestMethod.GET)
    public Map<String, BigDecimal> getUndistributedPriceFromMachine(@PathVariable Integer id) {
        return Collections.singletonMap("amount", vendingService.getUndistributedPriceFromMachine(id));
    }

    @RequestMapping(value = "/price/undistributed", method = RequestMethod.GET)
    public Map<String, BigDecimal> getUndistributedPrice() {
        return Collections.singletonMap("amount", vendingService.getUndistributedPrice());
    }
}
