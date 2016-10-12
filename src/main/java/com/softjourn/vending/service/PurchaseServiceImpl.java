package com.softjourn.vending.service;

import com.softjourn.vending.dao.PurchaseRepository;
import com.softjourn.vending.dto.PurchaseDTO;
import com.softjourn.vending.dto.PurchaseFilterDTO;
import com.softjourn.vending.entity.Purchase;
import com.softjourn.vending.enums.PurchaseDateEnum;
import com.softjourn.vending.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.softjourn.vending.utils.Constants.LAST_MONTH;
import static com.softjourn.vending.utils.Constants.LAST_WEEK;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private DateFormat dateFormat;

    @Override
    public Page<PurchaseDTO> getAllUsingFilter(PurchaseFilterDTO filter, Pageable pageable) throws ParseException {
        // -1 - by all machines
        if (filter.getMachineId() == -1) {
            // by any date
            if (filter.getType().equals(PurchaseDateEnum.Any.getType())) {
                return purchaseConverter(purchaseRepository.findAllByOrderByTimeDesc(pageable));
            } // by today's date
            else if (filter.getType().equals(PurchaseDateEnum.Today.getType())) {
                return purchaseConverter(purchaseRepository.findAllByTodaysDate(pageable));
            } // by last week
            else if (filter.getType().equals(PurchaseDateEnum.LastWeek.getType())) {
                return purchaseConverter(purchaseRepository.findAllByLastWeek(getPastDate(LAST_WEEK), pageable));
            } // by last month
            else if (filter.getType().equals(PurchaseDateEnum.LastMonth.getType())) {
                return purchaseConverter(purchaseRepository.findAllByLastMonth(getPastDate(LAST_MONTH), pageable));
            } // by Start due
            else if (filter.getType().equals(PurchaseDateEnum.StartDue.getType())) {
                return purchaseConverter(purchaseRepository.findAllByStartDue(dateFormat.parse(filter.getStart()),
                        dateFormat.parse(filter.getDue()), pageable));
            }
        } // otherwise by specific machine
        else {
            if (filter.getType().equals(PurchaseDateEnum.Any.getType())) {
                return purchaseConverter(purchaseRepository.findAllByMachineIdOrderByTimeDesc(filter.getMachineId(),
                        pageable));
            } // by today's date
            else if (filter.getType().equals(PurchaseDateEnum.Today.getType())) {
                return purchaseConverter(purchaseRepository.findAllByMachineIdByTodaysDate(filter.getMachineId(),
                        pageable));
            } // by last week
            else if (filter.getType().equals(PurchaseDateEnum.LastWeek.getType())) {
                return purchaseConverter(purchaseRepository.findAllByLastWeek(filter.getMachineId(),
                        getPastDate(LAST_WEEK), pageable));
            } // by last month
            else if (filter.getType().equals(PurchaseDateEnum.LastMonth.getType())) {
                return purchaseConverter(purchaseRepository.findAllByLastMonth(filter.getMachineId(),
                        getPastDate(LAST_MONTH), pageable));
            } // by Start due
            else if (filter.getType().equals(PurchaseDateEnum.StartDue.getType())) {
                return purchaseConverter(purchaseRepository.findAllByStartDue(filter.getMachineId(),
                        dateFormat.parse(filter.getStart()), dateFormat.parse(filter.getDue()), pageable));
            }
        }
        return null;
    }


    private Page<PurchaseDTO> purchaseConverter(Page<Purchase> purchases) {
        return purchases.map(purchase -> {
            PurchaseDTO dto = new PurchaseDTO();
            dto.setAccount(purchase.getAccount());
            dto.setDate(purchase.getTime());
            dto.setPrice(purchase.getProduct().getPrice());
            dto.setProduct(purchase.getProduct().getName());
            return dto;
        });
    }

    private Date getPastDate(Integer dayBefore) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, dayBefore);
        return cal.getTime();
    }

}
