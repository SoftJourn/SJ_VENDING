package com.softjourn.vending.dao;


import com.softjourn.vending.entity.VendingMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<VendingMachine, Integer>, RefreshableRepository<VendingMachine, Integer> {
    List<VendingMachine> findByIsActive(Boolean isActive);
}
