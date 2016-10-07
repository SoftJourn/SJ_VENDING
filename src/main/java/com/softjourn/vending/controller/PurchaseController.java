package com.softjourn.vending.controller;

import com.softjourn.vending.dto.PurchaseDTO;
import com.softjourn.vending.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/purchases", produces = MediaType.APPLICATION_JSON_VALUE)
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @RequestMapping(path = "/{machineId}", method = RequestMethod.GET)
    public List<PurchaseDTO> getAll(@PathVariable Integer machineId, @PageableDefault(size = 5) Pageable pageable) {
        return purchaseService.getAll(machineId, pageable);
    }

    @RequestMapping(path = "/today/{machineId}", method = RequestMethod.GET)
    public List<PurchaseDTO> getAllByToday(@PathVariable Integer machineId, @PageableDefault(size = 5) Pageable pageable) {
        return purchaseService.getAllByTodaysDate(machineId, pageable);
    }

//    @RequestMapping(path = "/week/{machineId}", method = RequestMethod.GET)
//    public List<PurchaseDTO> getAllByLastWeek(@PathVariable Integer machineId, @PageableDefault(size = 5) Pageable pageable) {
//        return purchaseService.getAllByLastWeek(machineId, pageable);
//    }
//
//    @RequestMapping(path = "/month/{machineId}", method = RequestMethod.GET)
//    public List<PurchaseDTO> getAllByLastMonth(@PathVariable Integer machineId, @PageableDefault(size = 5) Pageable pageable) {
//        return purchaseService.getAllByLastMonth(machineId, pageable);
//    }

}
