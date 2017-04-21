package com.softjourn.vending.service;

import com.softjourn.vending.dao.PurchaseRepository;
import com.softjourn.vending.dto.PurchaseFilterDTO;
import com.softjourn.vending.dto.SoldProductDTO;
import com.softjourn.vending.entity.Purchase;
import com.softjourn.vending.entity.VendingMachine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.softjourn.vending.controller.ControllerTestConfig.purchaseFilter;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseServiceTest {

    private PurchaseRepository purchaseRepository;

    private PurchaseServiceImpl purchaseService;

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
        this.purchaseService = new PurchaseServiceImpl(purchaseRepository, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        when(purchaseRepository.findAllByStartDue(any(), any(), any(), any())).thenReturn(new PageImpl<>(new ArrayList<Purchase>() {{
            add(new Purchase("ldap", "productName", new BigDecimal(10), new VendingMachine(), Instant.now()));
        }}));
        when(purchaseRepository.findAllByStartDue(any(), any(), any())).thenReturn(new PageImpl<>(new ArrayList<Purchase>() {{
            add(new Purchase("ldap", "productName", new BigDecimal(10), new VendingMachine(), Instant.now()));
        }}));
        List<SoldProductDTO> topResult = new ArrayList<>();
        for (long i = 10; i > 0; i--) {
            topResult.add(new SoldProductDTO("some", i));
        }
        when(purchaseRepository.findTopProductsByTime(any(Instant.class), any(Instant.class), any(PageRequest.class))).thenReturn(topResult);
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

    @Test
    public void topProductsByTimeRangeTest() {
        List<SoldProductDTO> topProductsByTimeRange = purchaseService.getTopProductsByTimeRange(10, "2016-10-06T04:00:00Z", "2016-10-06T04:00:00Z");
        assertEquals(10, topProductsByTimeRange.size());
        assertEquals(true, topProductsByTimeRange.get(0).getQuantity() >= topProductsByTimeRange.get(9).getQuantity());
    }

    @Test(expected = IllegalArgumentException.class)
    public void topProductsByTimeRangeTestWithWrongDateTime() {
        purchaseService.getTopProductsByTimeRange(10, "2016-10-06", "2016-10-06");
    }


}
