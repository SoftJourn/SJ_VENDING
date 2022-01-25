package com.softjourn.vending.service;

import com.softjourn.vending.dao.PurchaseRepository;
import com.softjourn.vending.dto.PurchaseDTO;
import com.softjourn.vending.dto.PurchaseFilterDTO;
import com.softjourn.vending.dto.SoldProductDTO;
import com.softjourn.vending.entity.Purchase;
import com.softjourn.vending.enums.PurchaseDateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.softjourn.vending.utils.Constants.LAST_MONTH;
import static com.softjourn.vending.utils.Constants.LAST_WEEK;

@Service
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;

    private final DateTimeFormatter dateTimeFormatter;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepository, DateTimeFormatter dateTimeFormatter) {
        this.purchaseRepository = purchaseRepository;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Page<PurchaseDTO> getAllUsingFilter(PurchaseFilterDTO filter, Pageable pageable) {
        // -1 - by all machines
        if (filter.getMachineId() == -1) {
            // by any date
            if (filter.getType().equals(PurchaseDateEnum.Any.getType())) {
                return purchaseConverter(purchaseRepository.findAllByOrderByTimeDesc(pageable));
            } // by today's date
            else if (filter.getType().equals(PurchaseDateEnum.Today.getType())) {
                return purchaseConverter(purchaseRepository.findAllByStartDue(getStartDate(LocalDate.now().toString(), filter.getTimeZoneOffSet()),
                        getDueDate(LocalDate.now().toString(), filter.getTimeZoneOffSet()), pageable));
            } // by last week
            else if (filter.getType().equals(PurchaseDateEnum.LastWeek.getType())) {
                return purchaseConverter(purchaseRepository.findAllByStartDue(getStartDate(getPastDate(LAST_WEEK), filter.getTimeZoneOffSet()),
                        getDueDate(LocalDate.now().toString(), filter.getTimeZoneOffSet()), pageable));
            } // by last month
            else if (filter.getType().equals(PurchaseDateEnum.LastMonth.getType())) {
                return purchaseConverter(purchaseRepository.findAllByStartDue(getStartDate(getPastDate(LAST_MONTH), filter.getTimeZoneOffSet()),
                        getDueDate(LocalDate.now().toString(), filter.getTimeZoneOffSet()), pageable));
            } // by Start due
            else if (filter.getType().equals(PurchaseDateEnum.StartDue.getType())) {
                return purchaseConverter(purchaseRepository.findAllByStartDue(getStartDate(filter.getStart(), filter.getTimeZoneOffSet()),
                        getDueDate(filter.getDue(), filter.getTimeZoneOffSet()), pageable));
            }
        } // otherwise by specific machine
        else {
            // by any date
            if (filter.getType().equals(PurchaseDateEnum.Any.getType())) {
                return purchaseConverter(purchaseRepository.findAllByMachineIdOrderByTimeDesc(filter.getMachineId(), pageable));
            } // by today's date
            else if (filter.getType().equals(PurchaseDateEnum.Today.getType())) {
                return purchaseConverter(purchaseRepository.findAllByStartDue(filter.getMachineId(), getStartDate(LocalDate.now().toString(), filter.getTimeZoneOffSet()),
                        getDueDate(LocalDate.now().toString(), filter.getTimeZoneOffSet()), pageable));
            } // by last week
            else if (filter.getType().equals(PurchaseDateEnum.LastWeek.getType())) {
                return purchaseConverter(purchaseRepository.findAllByStartDue(filter.getMachineId(), getStartDate(getPastDate(LAST_WEEK), filter.getTimeZoneOffSet()),
                        getDueDate(LocalDate.now().toString(), filter.getTimeZoneOffSet()), pageable));
            } // by last month
            else if (filter.getType().equals(PurchaseDateEnum.LastMonth.getType())) {
                return purchaseConverter(purchaseRepository.findAllByStartDue(filter.getMachineId(), getStartDate(getPastDate(LAST_MONTH), filter.getTimeZoneOffSet()),
                        getDueDate(LocalDate.now().toString(), filter.getTimeZoneOffSet()), pageable));
            } // by Start due
            else if (filter.getType().equals(PurchaseDateEnum.StartDue.getType())) {
                return purchaseConverter(purchaseRepository.findAllByStartDue(filter.getMachineId(), getStartDate(filter.getStart(), filter.getTimeZoneOffSet()),
                        getDueDate(filter.getDue(), filter.getTimeZoneOffSet()), pageable));
            }
        }
        return null;
    }

    /**
     * Method returns top products using incoming parameters
     * @param topSize - how many items are needed in list
     * @param start - (ISO format like: 2016-10-06T04:00:00Z)
     * @param due - (ISO format like: 2016-10-06T04:00:00Z)
     * @return List<SoldProductDTO>
     */
    @Override
    public List<SoldProductDTO> getTopProductsByTimeRange(Integer topSize, String start, String due) {
        try {
            Instant startTimestamp = Instant.parse(start);
            Instant dueTimestamp = Instant.parse(due);
            Pageable size = PageRequest.of(0, topSize);
            return this.purchaseRepository.findTopProductsByTime(startTimestamp, dueTimestamp, size);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Datetime field should be in ISO format(Example: 2016-10-06T04:00:00Z)");
        }
    }


    private Page<PurchaseDTO> purchaseConverter(Page<Purchase> purchases) {
        return purchases.map(purchase -> {
            PurchaseDTO dto = new PurchaseDTO();
            dto.setAccount(purchase.getAccount());
            dto.setDate(purchase.getTime());
            dto.setPrice(purchase.getProductPrice());
            dto.setProduct(purchase.getProductName());
            return dto;
        });
    }

    private String getPastDate(Integer daysBefore) {
        return LocalDate.now().minusDays(daysBefore).toString();
    }

    private Instant getStartDate(String start, Integer timeZoneOffSet) {
        LocalDate localDate = LocalDate.parse(start, dateTimeFormatter);
        return Instant.from(localDate.atStartOfDay(ZoneId.of(ZoneOffset.ofTotalSeconds(timeZoneOffSet * -1 * 60).getId())));
    }

    private Instant getDueDate(String due, Integer timeZoneOffSet) {
        LocalDate localDate = LocalDate.parse(due, dateTimeFormatter);
        return Instant.from(localDate.plusDays(1).atStartOfDay(ZoneId.of(ZoneOffset.ofTotalSeconds(timeZoneOffSet * -1 * 60).getId())));
    }

}
