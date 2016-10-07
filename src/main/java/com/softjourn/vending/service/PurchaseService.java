package com.softjourn.vending.service;

import com.softjourn.vending.dto.PurchaseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PurchaseService {

    List<PurchaseDTO> getAll(Integer machineId, Pageable pageable);

    List<PurchaseDTO> getAllByTodaysDate(Integer machineId, Pageable pageable);

//    List<PurchaseDTO> getAllByLastWeek(Integer machineId, Pageable pageable);
//
//    List<PurchaseDTO> getAllByLastMonth(Integer machineId, Pageable pageable);

}
