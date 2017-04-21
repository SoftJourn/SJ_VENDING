package com.softjourn.vending.dao;

import com.softjourn.vending.dto.SoldProductDTO;
import com.softjourn.vending.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p WHERE p.machine.id = ?1")
    List<Purchase> getAllByMachineId(Integer machineId);

    @Query("SELECT p FROM Purchase p WHERE p.account = ?1")
    List<Purchase> getAllByUser(String name);


    // -----------------------------------------------------------------------------------------------------------------
    // Without machine id
    Page<Purchase> findAllByOrderByTimeDesc(Pageable pageable);

    @Query(value = "SELECT p FROM Purchase p WHERE p.time >= ?1 AND p.time <= ?2 Order by p.time Desc")
    Page<Purchase> findAllByStartDue(Instant from, Instant to, Pageable pageable);

    // -----------------------------------------------------------------------------------------------------------------
    // With machine id
    Page<Purchase> findAllByMachineIdOrderByTimeDesc(Integer machineId, Pageable pageable);

    @Query(value = "SELECT p FROM Purchase p WHERE p.machine.id = ?1 AND p.time >= ?2 AND p.time <= ?3" +
            " Order by p.time Desc")
    Page<Purchase> findAllByStartDue(Integer machineId, Instant from, Instant to, Pageable pageable);

    @Query(value = "select new com.softjourn.vending.dto.SoldProductDTO(p.productName, count(p.productName)) from Purchase p" +
            " where p.time >= ?1 and p.time <= ?2 group by p.productName ORDER BY count(p.productName) DESC")
    List<SoldProductDTO> findTopProductsByTime(Instant start, Instant due, Pageable pageable);


}
