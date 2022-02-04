package com.softjourn.vending.service;

import com.softjourn.vending.dto.PurchaseDTO;
import com.softjourn.vending.dto.PurchaseFilterDTO;
import com.softjourn.vending.dto.SoldProductDTO;
import java.text.ParseException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseService {

  Page<PurchaseDTO> getAllUsingFilter(
      PurchaseFilterDTO filter, Pageable pageable
  ) throws ParseException;

  /**
   * Method get top sold products
   *
   * @param topSize
   * @param start (ISO format like: 2016-10-06T04:00:00Z)
   * @param due (ISO format like: 2016-10-06T04:00:00Z)
   * @return List<SoldProductDTO>
   */
  List<SoldProductDTO> getTopProductsByTimeRange(Integer topSize, String start, String due);
}
