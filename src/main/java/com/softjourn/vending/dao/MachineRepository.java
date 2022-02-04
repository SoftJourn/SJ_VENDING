package com.softjourn.vending.dao;

import com.softjourn.vending.entity.VendingMachine;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends RefreshableRepository<VendingMachine, Integer> {

  List<VendingMachine> findByIsActive(Boolean isActive);
}
