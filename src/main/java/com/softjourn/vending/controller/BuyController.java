package com.softjourn.vending.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.softjourn.vending.dto.FeatureDTO;
import com.softjourn.vending.dto.PurchaseProductDto;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.service.BuyService;
import com.softjourn.vending.service.VendingService;
import com.softjourn.vending.utils.jsonview.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize("authenticated")
@RequestMapping("/v1/machines")
public class BuyController {

    private BuyService buyService;
    private VendingService vendingService;

    @Autowired
    public BuyController(BuyService buyService, VendingService vendingService) {
        this.buyService = buyService;
        this.vendingService = vendingService;
    }

    @JsonView(View.Client.class)
    @RequestMapping(method = RequestMethod.GET)
    public Iterable<VendingMachine> getMachines() {
        return vendingService.getAll();
    }

    @JsonView(View.Client.class)
    @RequestMapping(value = "/{machineId}", method = RequestMethod.GET)
    public VendingMachine getMachine(@PathVariable Integer machineId) {
        return vendingService.get(machineId);
    }

    @RequestMapping(value = "/{machineId}/products", method = RequestMethod.GET)
    public Iterable<Product> getAvailableProducts(@PathVariable Integer machineId) {
        return buyService.getAvailableProducts(machineId);
    }

    @RequestMapping(value = "/{machineId}/features", method = RequestMethod.GET)
    public FeatureDTO getFeatures(@PathVariable Integer machineId) {
        return buyService.getFeatures(machineId);
    }

    @RequestMapping(value = "/last", method = RequestMethod.GET)
    public List<PurchaseProductDto> getLastPurchases(Principal principal) {
        return buyService.lastPurchases(principal);
    }

    @RequestMapping(value = "/{machineId}/fields/{fieldId}", method = RequestMethod.POST)
    public Map<String, BigDecimal> buyById(@PathVariable Integer machineId,
                                           @PathVariable String fieldId,
                                           Principal principal) {
        return Collections.singletonMap("amount", buyService.buy(machineId, fieldId, principal));
    }

    @RequestMapping(value = "/{machineId}/products/{productId}", method = RequestMethod.POST)
    public Map<String, BigDecimal> buyByProduct(@PathVariable Integer machineId,
                                                @PathVariable Integer productId,
                                                Principal principal) {
        return Collections.singletonMap("amount", buyService.buy(machineId, productId, principal));
    }

}
