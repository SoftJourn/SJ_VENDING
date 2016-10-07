package com.softjourn.vending.dao;

import com.softjourn.vending.entity.Purchase;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurchaseRepository extends CrudRepository<Purchase, Long> {
    @Query("SELECT p FROM Purchase p WHERE p.machine.id = ?1")
    List<Purchase> getAllByMachineId(Integer machineId);

    @Query("SELECT p FROM Purchase p WHERE p.account = ?1 AND p.machine.id = ?2")
    List<Purchase> getAllByUserAndMachine(String name, Integer machineId);

    List<Purchase> findAllByMachineIdOrderByTimeDesc(Integer machineId, Pageable pageable);

    @Query("SELECT p FROM Purchase p WHERE DATE(p.time) = CURDATE() AND p.machine.id = ?1 Order by p.time Desc")
    List<Purchase> findAllByTodaysDate(Integer machineId, Pageable pageable);

//    @Query(value = "SELECT p FROM Purchase p WHERE p.machine.id = ?1 AND DATE(p.time) BETWEEN (current_date() - 1 ) AND current_date()")
//    List<Purchase> findAllByLastWeek(Integer machineId, Pageable pageable);
//
//    @Query(value = "SELECT p FROM Purchase p WHERE p.machine.id = ?1 AND DATE(p.time) BETWEEN (CURDATE() INTERVAL 1 MONTH) AND CURDATE()")
//    List<Purchase> findAllByLastMonth(Integer machineId, Pageable pageable);

}
