package com.softjourn.vending.service;

import com.softjourn.vending.entity.Categories;

import java.util.List;

public interface CategoriesService {

    Categories save(Categories categories);

    List<Categories> getAll();

    Categories get(Long id);

    void delete(Long id);
}
