package com.softjourn.vending.dao;


import com.softjourn.vending.entity.Row;
import org.springframework.data.repository.CrudRepository;

public interface RowRepository extends CrudRepository<Row, Integer> {
}
