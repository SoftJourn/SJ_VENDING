package com.softjourn.vending.dao;

import com.softjourn.vending.entity.Purchase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurchaseRepository extends CrudRepository<Purchase, Double> {
    @Query("SELECT p FROM Purchase p WHERE p.machine = ?1")
    List<Purchase> getAllByMachineId(Integer machineId);
}
