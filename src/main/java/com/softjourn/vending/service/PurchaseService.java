package com.softjourn.vending.service;

import com.softjourn.vending.dto.PurchaseDTO;
import com.softjourn.vending.dto.PurchaseFilterDTO;
import com.softjourn.vending.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.util.List;

public interface PurchaseService {

    Page<PurchaseDTO> getAllUsingFilter(PurchaseFilterDTO filter, Pageable pageable) throws ParseException;

}
