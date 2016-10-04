package com.softjourn.vending.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FeatureDTO {

    private List<Integer> lastAdded;

    private List<Integer> bestSellers;

    private List<CategoryDTO> categories;

    public FeatureDTO(List<Integer> lastAdded, List<Integer> bestSellers, List<CategoryDTO> categories) {
        this.lastAdded = lastAdded;
        this.bestSellers = bestSellers;
        this.categories = categories;
    }
}
