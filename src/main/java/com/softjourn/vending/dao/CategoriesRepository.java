package com.softjourn.vending.dao;

import com.softjourn.vending.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Long> {

  Optional<Category> findOneByName(String name);
}
