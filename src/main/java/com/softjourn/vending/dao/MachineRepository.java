package com.softjourn.vending.dao;


import com.softjourn.vending.entity.VendingMachine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MachineRepository extends JpaRepository<VendingMachine, Integer> {
}
