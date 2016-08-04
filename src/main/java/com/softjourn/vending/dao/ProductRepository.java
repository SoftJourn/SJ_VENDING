package com.softjourn.vending.dao;


import com.softjourn.vending.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {
}
