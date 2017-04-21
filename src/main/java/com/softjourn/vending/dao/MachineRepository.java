package com.softjourn.vending.dao;


import com.softjourn.vending.entity.VendingMachine;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineRepository extends RefreshableRepository<VendingMachine, Integer> {
    List<VendingMachine> findByIsActive(Boolean isActive);
}
