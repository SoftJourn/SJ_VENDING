package com.softjourn.vending.controller;

import com.softjourn.vending.dto.PurchaseDTO;
import com.softjourn.vending.dto.PurchaseFilterDTO;
import com.softjourn.vending.entity.Purchase;
import com.softjourn.vending.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/v1/purchases", produces = MediaType.APPLICATION_JSON_VALUE)
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @RequestMapping(path = "/filter", method = RequestMethod.POST)
    public Page<PurchaseDTO> getAllByFilter(@Valid @RequestBody PurchaseFilterDTO filter,
                                            @PageableDefault(size = 5) Pageable pageable) throws ParseException {
        return purchaseService.getAllUsingFilter(filter, pageable);
    }

}
