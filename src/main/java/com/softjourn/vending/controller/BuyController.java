package com.softjourn.vending.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.softjourn.vending.dto.FeatureDTO;
import com.softjourn.vending.dto.PurchaseProductDto;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.service.BuyService;
import com.softjourn.vending.service.VendingService;
import com.softjourn.vending.utils.jsonview.View;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("authenticated")
@RequestMapping("/v1/machines")
public class BuyController {

  private final BuyService buyService;
  private final VendingService vendingService;

  @GetMapping
  @JsonView(View.Client.class)
  public Iterable<VendingMachine> getMachines() {
    return vendingService.getAllAvailable();
  }

  @GetMapping("/{machineId}")
  @JsonView(View.Client.class)
  public VendingMachine getMachine(@PathVariable Integer machineId) {
    return vendingService.get(machineId);
  }

  @GetMapping("/{machineId}/products")
  public Iterable<Product> getAvailableProducts(@PathVariable Integer machineId) {
    return buyService.getAvailableProducts(machineId);
  }

  @GetMapping("/{machineId}/products/{categoryName}")
  public List<Product> getAvailableProductsByCategory(
      @PathVariable Integer machineId, @PathVariable String categoryName
  ) {
    return buyService.getByCategoryName(categoryName, machineId);
  }

  @GetMapping("/{machineId}/features")
  public FeatureDTO getFeatures(@PathVariable Integer machineId) {
    return buyService.getFeatures(machineId);
  }

  @GetMapping("/last")
  public List<PurchaseProductDto> getLastPurchases(Principal principal) {
    return buyService.lastPurchases(principal);
  }

  @PostMapping("/{machineId}/fields/{fieldId}")
  public Map<String, BigDecimal> buyById(@PathVariable Integer machineId,
      @PathVariable String fieldId,
      Principal principal) {
    return Collections.singletonMap("amount", buyService.buy(machineId, fieldId, principal));
  }

  @PostMapping("/{machineId}/products/{productId}")
  public Map<String, BigDecimal> buyByProduct(
      @PathVariable Integer machineId,
      @PathVariable Integer productId,
      Principal principal
  ) {
    return Collections.singletonMap("amount", buyService.buy(machineId, productId, principal));
  }
}
