package com.softjourn.vending.service;

import com.softjourn.vending.dao.PurchaseRepository;
import com.softjourn.vending.dto.PurchaseFilterDTO;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.Purchase;
import com.softjourn.vending.entity.VendingMachine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.softjourn.vending.controller.ControllerTestConfig.purchaseFilter;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseServiceTest {

    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private PurchaseServiceImpl purchaseService = new PurchaseServiceImpl();

    PageRequest pageRequest = new PageRequest(0, 10);

    public PurchaseFilterDTO purchaseFilter2;
    public PurchaseFilterDTO purchaseFilter3;

    {
        // Zone - Europe/Warsaw
        purchaseFilter2 = new PurchaseFilterDTO(1, "Start-Due", -120, "2016-10-06", "2016-10-08");
        // Zone - US/Michigan
        purchaseFilter3 = new PurchaseFilterDTO(-1, "Start-Due", +240, "2016-10-06", "2016-10-08");
    }

    @Before
    public void prepareDependencies() throws ParseException {
        purchaseRepository = Mockito.mock(PurchaseRepository.class);
        MockitoAnnotations.initMocks(this);
        purchaseService.setDateTimeFormatter(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        when(purchaseRepository.findAllByStartDue(any(), any(), any(), any())).thenReturn(new PageImpl<>(new ArrayList<Purchase>() {{
            add(new Purchase("ldap", new Product(), new VendingMachine(), Instant.now()));
        }}));
        when(purchaseRepository.findAllByStartDue(any(), any(), any())).thenReturn(new PageImpl<>(new ArrayList<Purchase>() {{
            add(new Purchase("ldap", new Product(), new VendingMachine(), Instant.now()));
        }}));
    }

    @Test
    public void testVerifyZone() throws Exception {
        // Zone - Europe/Kiev
        purchaseService.getAllUsingFilter(purchaseFilter, pageRequest);
        verify(purchaseRepository).findAllByStartDue(1, Instant.parse("2016-10-05T21:00:00Z"),
                Instant.parse("2016-10-08T21:00:00Z"), pageRequest);
    }

    @Test
    public void testVerifyZone2() throws Exception {
        // Zone - Europe/Warsaw
        purchaseService.getAllUsingFilter(purchaseFilter2, pageRequest);
        verify(purchaseRepository).findAllByStartDue(1, Instant.parse("2016-10-05T22:00:00Z"),
                Instant.parse("2016-10-08T22:00:00Z"), pageRequest);
    }

    @Test
    public void testVerifyZone3() throws Exception {
        // Zone - US/Michigan
        purchaseService.getAllUsingFilter(purchaseFilter3, pageRequest);
        verify(purchaseRepository).findAllByStartDue(Instant.parse("2016-10-06T04:00:00Z"),
                Instant.parse("2016-10-09T04:00:00Z"), pageRequest);
    }


}
