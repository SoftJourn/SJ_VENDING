package com.softjourn.vending.dto;

import com.softjourn.vending.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryDTO {

    private String name;

    private List<Product> products;

}
