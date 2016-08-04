package com.softjourn.vending.service;


import com.softjourn.vending.dao.FieldRepository;
import com.softjourn.vending.dao.MachineRepository;
import com.softjourn.vending.dao.RowRepository;
import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VendingService {

    private MachineRepository repository;
    private RowRepository rowRepository;
    private FieldRepository fieldRepository;


    @Autowired
    public VendingService(MachineRepository repository, RowRepository rowRepository, FieldRepository fieldRepository) {
        this.repository = repository;
        this.rowRepository = rowRepository;
        this.fieldRepository = fieldRepository;
    }

    public Iterable<VendingMachine> getAll() {
        return repository.findAll();
    }

    public VendingMachine get(Integer id) {
        VendingMachine machine = repository.findOne(id);
        if(machine == null) throw new NotFoundException("Machine with such id not found.");
        return machine;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public VendingMachine create(VendingMachineBuilderDTO builder) {
        VendingMachine machine = new VendingMachine();
        machine.setName(builder.getName());

        List<Row> rows = getRows(builder);

        rows.stream()
                .peek(r -> fieldRepository.save(r.getFields()))
                .forEach(row -> rowRepository.save(row));

        machine.setRows(rows);
        return repository.save(machine);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(Integer id) {
        repository.delete(id);
    }

    private List<Row> getRows(VendingMachineBuilderDTO builder) {
        return numberingGenerator(builder.getRowsNumbering(), builder.getRowsCount())
                .map(Row::new)
                .peek(r -> r.setFields(getFields(builder.getColumnsNumbering(), builder.getColumnsCount(), r.getRowId())))
                .collect(Collectors.toList());
    }

    private List<Field> getFields(VendingMachineBuilderDTO.Numbering numbering, int count, String rowId) {
        return numberingGenerator(numbering, count)
                .map(s -> rowId + s)
                .reduce(new ArrayList<>(),
                        (l, s) -> {l.add(l.size(), new Field(s, l.size()));return l;},
                        (l1, l2) -> {l1.addAll(l2); return l1;});
    }

    private Stream<String> numberingGenerator(VendingMachineBuilderDTO.Numbering numbering, int count) {
        Stream<?> stream;
        switch (numbering) {
            case ALPHABETICAL:stream =  Stream.iterate('A', c -> (char)(c + 1));break;
            case NUMERICAL: default: stream = Stream.iterate(0, i ->  i + 1);
        }
        return stream.map(String::valueOf).limit(count);
    }


}
