package com.softjourn.vending.dao;

import com.softjourn.vending.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Integer> {

  @Query("SELECT p FROM Product p WHERE p.name LIKE %?1%")
  List<Product> getProductsByNameThatContain(String name);

  List<Product> getProductByCategory_Name(String name);

  Product getProductByName(String name);
}
