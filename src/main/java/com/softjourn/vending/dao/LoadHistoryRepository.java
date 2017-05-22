package com.softjourn.vending.dao;

import com.softjourn.vending.entity.LoadHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;


public interface LoadHistoryRepository extends JpaRepository<LoadHistory, Long> {

    @Modifying
    @Query("DELETE FROM LoadHistory lh WHERE lh.vendingMachine.id = :id")
    int deleteByMachineId(@Param("id") Integer id);

    @Query("Select l from LoadHistory l where l.vendingMachine.id = ?1 and l.dateAdded >= ?2 and l.dateAdded <= ?3")
    Page<LoadHistory> getLoadHistoryByVendingMachineAndTime(Integer machineId, Instant start, Instant due, Pageable pageable);

    @Query("Select l from LoadHistory l where l.vendingMachine.id = ?1")
    Page<LoadHistory> getLoadHistoryByVendingMachine(Integer machineId, Pageable pageable);

}
