package com.softjourn.vending.controller;


import com.softjourn.vending.dto.LoadHistoryRequestDTO;
import com.softjourn.vending.dto.LoadHistoryResponseDTO;
import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.service.FieldService;
import com.softjourn.vending.service.VendingService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;

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

    @RequestMapping(value = "/{id}/reset", method = RequestMethod.POST)
    public void resetEngines(@PathVariable Integer id) {
        vendingService.resetEngine(id);
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

    @RequestMapping(value = "/loads", method = RequestMethod.POST)
    public Page<LoadHistoryResponseDTO> getLoadHistoryByMachine(@RequestBody LoadHistoryRequestDTO requestDTO) {
        return this.vendingService.getLoadHistoryByFilter(requestDTO);
    }

    @RequestMapping(value = "/loads/export", method = RequestMethod.POST)
    public String exportLoadHistoryByMachine(@RequestBody LoadHistoryRequestDTO requestDTO, TimeZone timeZone)
            throws ReflectiveOperationException, IOException {
        Workbook workbook = vendingService.exportLoadHistory(requestDTO, timeZone);

        byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            bytes = bos.toByteArray();
        }

        return Base64.getEncoder().encodeToString(bytes);
    }

}
