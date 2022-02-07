package com.softjourn.vending.controller;

import com.softjourn.vending.dto.LoadHistoryRequestDTO;
import com.softjourn.vending.dto.LoadHistoryResponseDTO;
import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.service.FieldService;
import com.softjourn.vending.service.VendingService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/vending")
@PreAuthorize("hasAnyRole('INVENTORY','SUPER_ADMIN')")
public class VendingController {

  private final VendingService vendingService;
  private final FieldService fieldService;

  @GetMapping
  public Iterable<VendingMachine> getMachines() {
    return vendingService.getAll();
  }

  @GetMapping("/{id}")
  public VendingMachine getMachine(@PathVariable Integer id) {
    return vendingService.get(id);
  }

  @PostMapping
  public VendingMachine add(
      @RequestBody VendingMachineBuilderDTO machineBuilder, Principal principal
  ) {
    return vendingService.create(machineBuilder, principal);
  }

  @PutMapping("/update")
  public VendingMachine update(@RequestBody VendingMachine machine) {
    return vendingService.update(machine);
  }

  @PostMapping("/{id}/reset")
  public void resetEngines(@PathVariable Integer id) {
    vendingService.resetEngine(id);
  }

  @PutMapping
  public VendingMachine refill(@RequestBody VendingMachine machine, Principal principal) {
    return vendingService.refill(machine, principal);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    vendingService.delete(id);
  }

  @PostMapping("/{id}/fields/{fieldId}")
  public Field updateField(@PathVariable Integer id,
      @PathVariable Integer fieldId,
      @RequestBody Field field) {
    return fieldService.update(fieldId, field, id);
  }

  @PostMapping("/{id}/rows/{rowId}")
  public Row updateRow(@PathVariable Integer id,
      @PathVariable Integer rowId,
      @RequestBody Integer count) {
    return fieldService.updateFieldsCountInRow(rowId, count);
  }

  @GetMapping("/price")
  @PreAuthorize("authenticated")
  public Map<String, BigDecimal> getAllMachinesLoadedPrice() {
    return Collections.singletonMap("amount", vendingService.getLoadedPrice());
  }

  @GetMapping("/{id}/price")
  public Map<String, BigDecimal> getMachineLoadedPrice(@PathVariable Integer id) {
    return Collections.singletonMap("amount", vendingService.getLoadedPrice(id));
  }

  @PostMapping("/loads")
  public Page<LoadHistoryResponseDTO> getLoadHistoryByMachine(
      @RequestBody LoadHistoryRequestDTO requestDTO
  ) {
    return this.vendingService.getLoadHistoryByFilter(requestDTO);
  }

  @PostMapping("/loads/export")
  public String exportLoadHistoryByMachine(
      @RequestBody LoadHistoryRequestDTO requestDTO, TimeZone timeZone
  ) throws ReflectiveOperationException, IOException {
    Workbook workbook = vendingService.exportLoadHistory(requestDTO, timeZone);

    byte[] bytes;
    try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      workbook.write(bos);
      bytes = bos.toByteArray();
    }

    return Base64.getEncoder().encodeToString(bytes);
  }
}
