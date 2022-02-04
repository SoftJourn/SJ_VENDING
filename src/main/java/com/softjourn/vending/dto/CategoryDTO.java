package com.softjourn.vending.dto;

import com.softjourn.vending.entity.Product;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDTO {

  private String name;
  private List<Product> products;
}
