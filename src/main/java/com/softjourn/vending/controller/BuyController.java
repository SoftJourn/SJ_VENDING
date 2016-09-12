package com.softjourn.vending.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.softjourn.vending.dao.CategoriesRepository;
import com.softjourn.vending.dto.ProductDTO;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.service.BuyService;
import com.softjourn.vending.service.CategoriesService;
import com.softjourn.vending.service.VendingService;
import com.softjourn.vending.utils.jsonview.View;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/machines")
public class BuyController {

    private BuyService buyService;
    private VendingService vendingService;

    @Autowired
    private CategoriesService categoriesService;

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
    public Iterable<ProductDTO> getAvailableProducts(@PathVariable Integer machineId) {
        return buyService.getAvailableProducts(machineId);
    }

    @RequestMapping(value = "/{machineId}/features", method = RequestMethod.GET)
    public Map<String, List<?>> getFeatures(@PathVariable Integer machineId, Principal principal) {
        Map<String, List<?>> result = new HashMap<>();
        result.put("New products", buyService.getNew(machineId));
        result.put("My lastPurchases", buyService.lastPurchases(principal, machineId));
        result.put("Best sellers", buyService.getBestSellers(machineId));
        categoriesService.getAll()
                .forEach(c -> result.put(c.getName(), buyService.getByCategory(c, machineId)));
        return result;
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
