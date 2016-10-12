package com.softjourn.vending.dao;

import com.softjourn.vending.entity.Purchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    @Query("SELECT p FROM Purchase p WHERE p.machine.id = ?1")
    List<Purchase> getAllByMachineId(Integer machineId);

    @Query("SELECT p FROM Purchase p WHERE p.account = ?1")
    List<Purchase> getAllByUser(String name);


    // -----------------------------------------------------------------------------------------------------------------
    // Without machine id
    Page<Purchase> findAllByOrderByTimeDesc(Pageable pageable);

    @Query("SELECT p FROM Purchase p WHERE DATE(p.time) = CURDATE() Order by p.time Desc")
    Page<Purchase> findAllByTodaysDate(Pageable pageable);

    @Query(value = "SELECT p FROM Purchase p WHERE DATE(p.time) BETWEEN ?1 AND CURDATE()" +
            " Order by p.time Desc")
    Page<Purchase> findAllByLastWeek(Date from, Pageable pageable);

    @Query(value = "SELECT p FROM Purchase p WHERE DATE(p.time) BETWEEN ?1 AND CURDATE()" +
            " Order by p.time Desc")
    Page<Purchase> findAllByLastMonth(Date from, Pageable pageable);

    @Query(value = "SELECT p FROM Purchase p WHERE DATE(p.time) BETWEEN ?1 AND ?2" +
            " Order by p.time Desc")
    Page<Purchase> findAllByStartDue(Date from, Date to, Pageable pageable);

    // -----------------------------------------------------------------------------------------------------------------
    // With machine id
    Page<Purchase> findAllByMachineIdOrderByTimeDesc(Integer machineId, Pageable pageable);

    @Query("SELECT p FROM Purchase p WHERE DATE(p.time) = CURDATE() AND p.machine.id = ?1 Order by p.time Desc")
    Page<Purchase> findAllByMachineIdByTodaysDate(Integer machineId, Pageable pageable);

    @Query(value = "SELECT p FROM Purchase p WHERE p.machine.id = ?1 AND DATE(p.time) BETWEEN ?2 AND CURDATE() Order by p.time Desc")
    Page<Purchase> findAllByLastWeek(Integer machineId, Date from, Pageable pageable);

    @Query(value = "SELECT p FROM Purchase p WHERE p.machine.id = ?1 AND DATE(p.time) BETWEEN ?2 AND CURDATE() Order by p.time Desc")
    Page<Purchase> findAllByLastMonth(Integer machineId, Date from, Pageable pageable);

    @Query(value = "SELECT p FROM Purchase p WHERE p.machine.id = ?1 AND DATE(p.time) BETWEEN ?2 AND ?3" +
            " Order by p.time Desc")
    Page<Purchase> findAllByStartDue(Integer machineId, Date from, Date to, Pageable pageable);

}
