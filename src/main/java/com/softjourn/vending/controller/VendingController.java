package com.softjourn.vending.controller;


import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.service.FieldService;
import com.softjourn.vending.service.VendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/vending")
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
    public VendingMachine add(@RequestBody VendingMachineBuilderDTO machineBuilder) {
        return vendingService.create(machineBuilder);
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

}
