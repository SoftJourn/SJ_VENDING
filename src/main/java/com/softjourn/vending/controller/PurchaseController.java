package com.softjourn.vending.controller;

import com.softjourn.vending.dto.PurchaseDTO;
import com.softjourn.vending.dto.PurchaseFilterDTO;
import com.softjourn.vending.dto.SoldProductDTO;
import com.softjourn.vending.dto.TopProductsDTO;
import com.softjourn.vending.service.PurchaseService;
import java.text.ParseException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('INVENTORY','SUPER_ADMIN')")
@RequestMapping(value = "/v1/purchases", produces = MediaType.APPLICATION_JSON_VALUE)
public class PurchaseController {

  private final PurchaseService purchaseService;

  @PostMapping("/filter")
  public Page<PurchaseDTO> getAllByFilter(@Valid @RequestBody PurchaseFilterDTO filter,
      @PageableDefault(size = 5) Pageable pageable) throws ParseException {
    return purchaseService.getAllUsingFilter(filter, pageable);
  }

  @PostMapping("/top")
  public List<SoldProductDTO> getTopProducts(@Valid @RequestBody TopProductsDTO topProducts) {
    return purchaseService.getTopProductsByTimeRange(
        topProducts.getTopSize(),topProducts.getStart(), topProducts.getDue());
  }
}
