package com.softjourn.vending.dao;

import com.softjourn.vending.dto.SoldProductDTO;
import com.softjourn.vending.entity.Purchase;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

  @Query("SELECT p FROM Purchase p WHERE p.machine.id = ?1")
  List<Purchase> getAllByMachineId(Integer machineId);

  @Query("SELECT p FROM Purchase p WHERE p.account = ?1")
  List<Purchase> getAllByUser(String name);


  // -----------------------------------------------------------------------------------------------
  // Without machine id
  Page<Purchase> findAllByOrderByTimeDesc(Pageable pageable);

  @Query("SELECT p FROM Purchase p WHERE p.time >= ?1 AND p.time <= ?2 ORDER BY p.time DESC")
  Page<Purchase> findAllByStartDue(Instant from, Instant to, Pageable pageable);

  // -----------------------------------------------------------------------------------------------
  // With machine id
  Page<Purchase> findAllByMachineIdOrderByTimeDesc(Integer machineId, Pageable pageable);

  @Query("SELECT p FROM Purchase p WHERE p.machine.id = ?1 AND p.time >= ?2 AND p.time <= ?3" +
      " ORDER BY p.time DESC")
  Page<Purchase> findAllByStartDue(Integer machineId, Instant from, Instant to, Pageable pageable);

  @Query("SELECT new com.softjourn.vending.dto.SoldProductDTO(p.productName, count(p.productName)) "
      + "FROM Purchase p " + ""
      + "WHERE p.time >= ?1 AND p.time <= ?2 "
      + "GROUP BY p.productName ORDER BY count(p.productName) DESC")
  List<SoldProductDTO> findTopProductsByTime(Instant start, Instant due, Pageable pageable);
}
