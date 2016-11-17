package com.softjourn.vending.dao;


import com.softjourn.vending.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Integer> {

    @Query("Select p from Product p where p.name like %?1%")
    List<Product> getProductsByNameThatContain(String name);

    List<Product> getProductByCategory_Name(String name);
}
