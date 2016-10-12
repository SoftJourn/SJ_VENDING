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
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VendingService {

    private MachineRepository machineRepository;
    private RowRepository rowRepository;
    private FieldRepository fieldRepository;
    private CoinService coinService;


    @Autowired
    public VendingService(MachineRepository machineRepository,
                          RowRepository rowRepository,
                          FieldRepository fieldRepository,
                          CoinService coinService) {
        this.machineRepository = machineRepository;
        this.rowRepository = rowRepository;
        this.fieldRepository = fieldRepository;
        this.coinService = coinService;
    }

    public VendingMachine refill(VendingMachine machine, Principal principal) {
        List<Field> fields = machine.getFields();

        BigDecimal loadedPrice = fields.stream()
                .filter(field -> field.getProduct() != null)
                .map(field -> field.getProduct().getPrice().multiply(new BigDecimal(field.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

//        coinService.refill(principal, loadedPrice, machine.getName());
        return machineRepository.save(machine);
    }

    public Iterable<VendingMachine> getAll() {
        return machineRepository.findAll();
    }

    public VendingMachine get(Integer id) {
        VendingMachine machine = machineRepository.findOne(id);
        if(machine == null) throw new NotFoundException("Machine with such id not found.");
        return machine;
    }

    @Transactional
    public VendingMachine create(VendingMachineBuilderDTO builder) {
        VendingMachine machine = new VendingMachine();
        machine.setName(builder.getName());

        List<Row> rows = getRows(builder);

        rows.stream()
                .peek(r -> fieldRepository.save(r.getFields()))
                .forEach(row -> rowRepository.save(row));

        machine.setRows(rows);
        return machineRepository.save(machine);
    }

    public void delete(Integer id) {
        machineRepository.delete(id);
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
