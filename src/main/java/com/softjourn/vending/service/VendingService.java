package com.softjourn.vending.service;


import com.softjourn.vending.dao.FieldRepository;
import com.softjourn.vending.dao.LoadHistoryRepository;
import com.softjourn.vending.dao.MachineRepository;
import com.softjourn.vending.dao.RowRepository;
import com.softjourn.vending.dto.MerchantDTO;
import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.LoadHistory;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.exceptions.ErisAccountNotFoundException;
import com.softjourn.vending.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VendingService {

    @Value("${coins.server.host}")
    private String coinsServerHost;

    private MachineRepository machineRepository;
    private RowRepository rowRepository;
    private FieldRepository fieldRepository;
    private LoadHistoryRepository loadHistoryRepository;
    private RestTemplate coinRestTemplate;


    @Autowired
    public VendingService(MachineRepository machineRepository,
                          RowRepository rowRepository,
                          FieldRepository fieldRepository,
                          LoadHistoryRepository loadHistoryRepository,
                          CoinService coinService) {
        this.machineRepository = machineRepository;
        this.rowRepository = rowRepository;
        this.fieldRepository = fieldRepository;
        this.loadHistoryRepository = loadHistoryRepository;

        coinRestTemplate = new RestTemplate();
    }

    @Transactional
    public VendingMachine refill(VendingMachine machine, Principal principal) {
        saveLoadHistory(machine, false);

        return machineRepository.save(machine);
    }

    public List<VendingMachine> getAll() {
        return machineRepository.findAll();
    }

    public VendingMachine get(Integer id) {
        VendingMachine machine = machineRepository.findOne(id);
        if (machine == null) throw new NotFoundException("Machine with such id not found.");
        return machine;
    }

    @Transactional
    public VendingMachine create(VendingMachineBuilderDTO builder, Principal principal) {
        VendingMachine machine = new VendingMachine();
        machine.setUniqueId(UUID.randomUUID().toString());
        machine.setName(builder.getName());
        machine.setUrl(builder.getUrl());

        List<Row> rows = getRows(builder);

        rows.stream()
                .peek(r -> fieldRepository.save(r.getFields()))
                .forEach(row -> rowRepository.save(row));

        machine.setRows(rows);

        MerchantDTO merchantDTO = new MerchantDTO(machine.getName(), machine.getUniqueId());

        try {
            coinRestTemplate.exchange(coinsServerHost + "/account/merchant",
                    HttpMethod.POST,
                    prepareRequest(merchantDTO, principal), Map.class);
        } catch (RestClientException e) {
            throw new ErisAccountNotFoundException(machine.getName());
        }

        VendingMachine vendingMachine = machineRepository.save(machine);

        saveLoadHistory(vendingMachine, true);

        return vendingMachine;
    }

    public VendingMachine update(VendingMachine machine) {
        VendingMachine machineToUpdate = this.machineRepository.getOne(machine.getId());
        machineToUpdate.setName(machine.getName());
        machineToUpdate.setUrl(machine.getUrl());
        return this.machineRepository.save(machineToUpdate);
    }

    private LoadHistory saveLoadHistory(VendingMachine vendingMachine, Boolean isDistributed) {
        VendingMachine oldMachineState = Optional
                .ofNullable(machineRepository.findOne(vendingMachine.getId()))
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Machine with id %d was not found",
                        vendingMachine.getId())));

        BigDecimal previousMachinePrice = getLoadedPrice(oldMachineState);
        BigDecimal machinePrice = getLoadedPrice(vendingMachine);

        LoadHistory loadHistory = new LoadHistory();
        loadHistory.setPrice(machinePrice.subtract(previousMachinePrice));
        loadHistory.setDateAdded(Instant.now());
        loadHistory.setIsDistributed(isDistributed);
        loadHistory.setVendingMachine(vendingMachine);

        return loadHistoryRepository.save(loadHistory);
    }

    private HttpEntity<Object> prepareRequest(Principal principal) {
        return new HttpEntity<>(new HttpHeaders() {{
            put("Authorization", Collections.singletonList(getTokenHeader(principal)));
        }});
    }

    private HttpEntity<Object> prepareRequest(Object body, Principal principal) {
        return new HttpEntity<>(body, new HttpHeaders() {{
            put("Authorization", Collections.singletonList(getTokenHeader(principal)));
        }});
    }

    private String getTokenHeader(Principal principal) {
        if (principal instanceof OAuth2Authentication) {
            OAuth2AuthenticationDetails authenticationDetails = (OAuth2AuthenticationDetails) ((OAuth2Authentication) principal).getDetails();
            return authenticationDetails.getTokenType() + " " + authenticationDetails.getTokenValue();
        } else {
            throw new IllegalStateException("Unsupported authentication type.");
        }
    }

    @Transactional
    public void delete(Integer id, Principal principal) {
        VendingMachine machine = machineRepository.findOne(id);

        loadHistoryRepository.deleteByMachineId(machine.getId());
        machineRepository.delete(machine);

        try {
            coinRestTemplate.exchange(coinsServerHost + "/account/" + machine.getUniqueId(),
                    HttpMethod.DELETE,
                    prepareRequest(principal), Map.class);
        } catch (RestClientException e) {
            throw new ErisAccountNotFoundException(machine.getUniqueId());
        }
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
                        (l, s) -> {
                            l.add(l.size(), new Field(s, l.size()));
                            return l;
                        },
                        (l1, l2) -> {
                            l1.addAll(l2);
                            return l1;
                        });
    }

    private Stream<String> numberingGenerator(VendingMachineBuilderDTO.Numbering numbering, int count) {
        Stream<?> stream;
        switch (numbering) {
            case ALPHABETICAL:
                stream = Stream.iterate('A', c -> (char) (c + 1));
                break;
            case NUMERICAL:
            default:
                stream = Stream.iterate(1, i -> i + 1);
        }
        return stream.map(String::valueOf).limit(count);
    }

    public BigDecimal getLoadedPrice() {
        return machineRepository.findAll().stream()
                .map(this::getLoadedPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getLoadedPrice(Integer id) {
        return Optional.ofNullable(machineRepository.findOne(id))
                .map(this::getLoadedPrice)
                .orElseThrow(() -> new NotFoundException(String.format("Machine with id %d was not found", id)));
    }

    private BigDecimal getLoadedPrice(VendingMachine vendingMachine) {
        return vendingMachine.getFields().stream()
                .filter(field -> field.getProduct() != null)
                .map(field -> field.getProduct().getPrice().multiply(new BigDecimal(field.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getUndistributedPrice() {
        return loadHistoryRepository.getUndistributedPrice().orElse(BigDecimal.ZERO);
    }

    public BigDecimal getUndistributedPriceFromMachine(Integer id) {
        return loadHistoryRepository.getUndistributedPriceFromMachine(id).orElse(BigDecimal.ZERO);
    }
}
