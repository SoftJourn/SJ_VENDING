package com.softjourn.vending.service;

import com.softjourn.vending.dao.CategoriesRepository;
import com.softjourn.vending.entity.Categories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Override
    public Categories save(Categories categories) {
        return categoriesRepository.save(categories);
    }

    @Override
    public List<Categories> getAll() {
        return categoriesRepository.findAll();
    }

    @Override
    public Categories get(Long id) {
        return categoriesRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        categoriesRepository.delete(id);
    }
}
