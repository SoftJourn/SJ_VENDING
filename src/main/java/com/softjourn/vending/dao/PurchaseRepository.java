package com.softjourn.vending.dao;

import com.softjourn.vending.entity.Purchase;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {
    @Query("SELECT p FROM Purchase p WHERE p.machine.id = ?1")
    List<Purchase> getAllByMachineId(Integer machineId);

    @Query("SELECT p FROM Purchase p WHERE p.account = ?1 AND p.machine.id = ?2")
    List<Purchase> getAllByUserAndMachine(String name, Integer machineId);
}
