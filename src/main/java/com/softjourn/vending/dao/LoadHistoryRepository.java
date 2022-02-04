package com.softjourn.vending.dao;

import com.softjourn.vending.entity.LoadHistory;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoadHistoryRepository extends JpaRepository<LoadHistory, Long> {

  @Modifying
  @Query("DELETE FROM LoadHistory lh WHERE lh.vendingMachine.id = :id")
  int deleteByMachineId(@Param("id") Integer id);

  @Query("SELECT l FROM LoadHistory l "
      + "WHERE l.vendingMachine.id = ?1 AND l.dateAdded >= ?2 AND l.dateAdded <= ?3")
  Page<LoadHistory> getLoadHistoryByVendingMachineAndTime(
      Integer machineId, Instant start, Instant due, Pageable pageable);

  @Query("SELECT l FROM LoadHistory l WHERE l.vendingMachine.id = ?1")
  Page<LoadHistory> getLoadHistoryByVendingMachine(Integer machineId, Pageable pageable);
}
