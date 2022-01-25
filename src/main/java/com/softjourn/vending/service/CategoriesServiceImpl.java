package com.softjourn.vending.service;

import com.softjourn.vending.dao.CategoriesRepository;
import com.softjourn.vending.entity.Category;
import com.softjourn.vending.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoriesRepository;

    @Autowired
    public CategoriesServiceImpl(CategoriesRepository categoriesRepository) {
        this.categoriesRepository = categoriesRepository;
    }

    @Override
    public Category save(Category category) {
        return categoriesRepository.save(category);
    }

    @Override
    public List<Category> getAll() {
        return categoriesRepository.findAll();
    }

    @Override
    public Category get(Long id) {
        return categoriesRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        categoriesRepository.deleteById(id);
    }

    @Override
    public Category getByName(String categoryName) throws NotFoundException {
        return categoriesRepository.findOneByName(categoryName)
                .orElseThrow(() -> {
                    String message = String.format(
                            "Category with name %s not found",
                            categoryName);
                    return new NotFoundException(message);
                });
    }
}
