package com.softjourn.vending.dao;


import com.softjourn.vending.entity.Field;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface FieldRepository extends CrudRepository<Field, Integer> {

    @Query("SELECT " +
            "case when count(f) > 0 then true else false end " +
            "FROM VendingMachine m " +
            "JOIN m.rows r " +
            "JOIN r.fields f " +
            "WHERE f.internalId = ?1 AND m.id = ?2")
    boolean checkIfThereIsSameInternalId(String internalId, Integer machineId);

}
