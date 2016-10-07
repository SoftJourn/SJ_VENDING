package com.softjourn.vending.service;

import com.softjourn.vending.dao.PurchaseRepository;
import com.softjourn.vending.dto.PurchaseDTO;
import com.softjourn.vending.entity.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Override
    public List<PurchaseDTO> getAll(Integer machineId, Pageable pageable) {
        return purchaseConverter(purchaseRepository.findAllByMachineIdOrderByTimeDesc(machineId, pageable));
    }

    @Override
    public List<PurchaseDTO> getAllByTodaysDate(Integer machineId, Pageable pageable) {
        return purchaseConverter(purchaseRepository.findAllByTodaysDate(machineId, pageable));
    }

//    @Override
//    public List<PurchaseDTO> getAllByLastWeek(Integer machineId, Pageable pageable) {
//        return purchaseConverter(purchaseRepository.findAllByLastWeek(machineId, pageable));
//    }
//
//    @Override
//    public List<PurchaseDTO> getAllByLastMonth(Integer machineId, Pageable pageable) {
//        return purchaseConverter(purchaseRepository.findAllByLastMonth(machineId, pageable));
//    }

    private List<PurchaseDTO> purchaseConverter(List<Purchase> purchases) {
        return purchases.stream().map(purchase -> {
            PurchaseDTO dto = new PurchaseDTO();
            dto.setAccount(purchase.getAccount());
            dto.setDate(purchase.getTime());
            dto.setPrice(purchase.getProduct().getPrice());
            dto.setProduct(purchase.getProduct().getName());
            return dto;
        }).collect(Collectors.toList());
    }

}
