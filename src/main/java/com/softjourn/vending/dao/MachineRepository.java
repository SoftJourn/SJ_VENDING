package com.softjourn.vending.dao;


import com.softjourn.vending.entity.VendingMachine;
import org.springframework.data.repository.CrudRepository;

public interface MachineRepository extends CrudRepository<VendingMachine, Integer> {
}
