package com.softjourn.vending.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureDTO {

  private List<Integer> lastAdded;
  private List<Integer> bestSellers;
  private List<CategoryDTO> categories;
}
