package com.softjourn.vending.service;

import com.softjourn.vending.entity.Category;
import com.softjourn.vending.exceptions.NotFoundException;

import java.util.List;

public interface CategoriesService {

    Category save(Category category);

    List<Category> getAll();

    Category get(Long id);

    void delete(Long id);

    Category getByName(String categoryName) throws NotFoundException;
}
