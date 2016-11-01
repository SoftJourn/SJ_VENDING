package com.softjourn.vending.dao;

import com.softjourn.vending.entity.LoadHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;


public interface LoadHistoryRepository extends JpaRepository<LoadHistory, Long> {

    public LoadHistory findFirst1ByVendingMachine_IdOrderByDateAddedDesc(Integer id);

    @Query("SELECT SUM(lh.price) FROM LoadHistory lh WHERE lh.isDistributed = FALSE")
    public Optional<BigDecimal> getUndistributedPrice();

    @Query("SELECT SUM(lh.price) FROM LoadHistory lh WHERE lh.vendingMachine.id = :id AND lh.isDistributed = FALSE")
    public Optional<BigDecimal> getUndistributedPriceFromMachine(@Param("id") Integer id);

    @Modifying
    @Query("UPDATE LoadHistory lh SET lh.isDistributed = TRUE WHERE lh.isDistributed = FALSE")
    public int updateHistoriesAfterDistribution();

    @Modifying
    @Query("DELETE FROM LoadHistory lh WHERE lh.vendingMachine.id = :id")
    public int deleteByMachineId(@Param("id") Integer id);
}
