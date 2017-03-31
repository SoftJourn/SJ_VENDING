package com.softjourn.vending.service;


import com.softjourn.common.functions.OptionalUtil;
import com.softjourn.vending.dao.FieldRepository;
import com.softjourn.vending.dao.LoadHistoryRepository;
import com.softjourn.vending.dao.MachineRepository;
import com.softjourn.vending.dao.RowRepository;
import com.softjourn.vending.dto.MerchantDTO;
import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.*;
import com.softjourn.vending.exceptions.BadRequestException;
import com.softjourn.vending.exceptions.ErisAccountNotFoundException;
import com.softjourn.vending.exceptions.MachineNotFoundException;
import com.softjourn.vending.exceptions.NotFoundException;
import com.softjourn.vending.utils.ReflectionMergeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
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
import java.util.concurrent.atomic.AtomicBoolean;
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

    private MachineService machineService;

    private ReflectionMergeUtil<Field> fieldMergeUtil;


    @Autowired
    public VendingService(MachineRepository machineRepository,
                          RowRepository rowRepository,
                          FieldRepository fieldRepository,
                          LoadHistoryRepository loadHistoryRepository,
                          CoinService coinService,
                          @Lazy MachineService machineService) {
        this.machineRepository = machineRepository;
        this.rowRepository = rowRepository;
        this.fieldRepository = fieldRepository;
        this.loadHistoryRepository = loadHistoryRepository;
        this.machineService = machineService;

        coinRestTemplate = new RestTemplate();

        fieldMergeUtil = ReflectionMergeUtil
                .forClass(Field.class)
                .ignoreNull(true)
                .build();
    }

    static boolean checkCellLimit(VendingMachine machine) {
        return machine.getFields().stream().allMatch(cell ->
                        cell.getCount() <= machine.getCellLimit() && cell.getCount() > -1);
    }

    @Transactional
    public VendingMachine refill(VendingMachine machine, Principal principal) {

        VendingMachine machineToUpdate = machineRepository.findOne(machine.getId());
        if (machineToUpdate == null) {
            throw new BadRequestException("Requested machine does not exists");
        }
        machine.setCellLimit(machineToUpdate.getCellLimit());
        if (!VendingService.checkCellLimit(machine)) {
            throw new BadRequestException("Requested machine cell out of limit '"
                    + machineToUpdate.getCellLimit() + "'");
        }
        updateFields(machineToUpdate, machine);
        saveLoadHistory(machineToUpdate, false);

        machineRepository.refresh(machineToUpdate);
        return machineToUpdate;
    }

    public List<VendingMachine> getAll() {
        return machineRepository.findAll();
    }

    public List<VendingMachine> getAllAvailable() {
        return machineRepository.findByIsActive(true);
    }

    public VendingMachine get(Integer id) {
        VendingMachine machine = machineRepository.findOne(id);
        if (machine == null)
            throw new MachineNotFoundException(String.format("There is not such machine with id %d", id));
        return machine;
    }

    @Transactional
    public VendingMachine create(VendingMachineBuilderDTO builder, Principal principal) {
        VendingMachine machine = new VendingMachine();

        try {
            machine.setUniqueId(UUID.randomUUID().toString());
            machine.setName(builder.getName());
            machine.setUrl(builder.getUrl());
            machine.setCellLimit(builder.getCellLimit());
            machine.setIsActive(builder.getIsActive());
            List<Row> rows = getRows(builder);
            rows.stream()
                    .peek(r -> fieldRepository.save(r.getFields()))
                    .forEach(row -> rowRepository.save(row));

            machine.setRows(rows);
        } catch (NullPointerException e) {
            throw new BadRequestException("Request has null values");
        }

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
        machineToUpdate.setIsActive(machine.getIsActive());
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

    private void updateFields(VendingMachine machineToUpdate, VendingMachine machine) {
        machine.getFields().stream()
                .filter(field -> countChanged(field, machineToUpdate) || productChanged(field, machineToUpdate))
                .map(field -> fieldMergeUtil.merge(getField(machineToUpdate, field.getId()), field))
                .peek(field -> field.setLoaded(Instant.now()))
                .forEach(s -> fieldRepository.saveAndFlush(s));
    }

    boolean productChanged(Field field, VendingMachine machine) {
        Integer savedFieldProductId = OptionalUtil.allChainOrElse(getField(machine, field.getId()), Field::getProduct, Product::getId, null);
        Integer receivedFieldProductId = OptionalUtil.allChainOrElse(field, Field::getProduct, Product::getId, null);

        if (savedFieldProductId == null)
            return receivedFieldProductId != null;
        else
            return !savedFieldProductId.equals(receivedFieldProductId);
    }

    private boolean countChanged(Field field, VendingMachine machine) {
        return !getField(machine, field.getId()).getCount().equals(field.getCount());
    }

    private Field getField(VendingMachine vendingMachine, Integer id) {
        return vendingMachine.getFields().stream()
                .filter(field -> field.getId().equals(id))
                .findAny()
                .orElseThrow(() -> new NotFoundException("Field with id " + id + " not found in machine " + vendingMachine.getName() + "."));
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
    public void delete(Integer id) {
        loadHistoryRepository.deleteByMachineId(id);
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

    public void resetEngine(Integer id) {
        AtomicBoolean isActive = new AtomicBoolean();
        Optional.ofNullable(get(id))
                .map(machine -> {
                    isActive.set(machine.getIsActive());
                    machine.setIsActive(false);
                    return machine;
                })
                .map(machine -> machineRepository.save(machine))
                .map(machine -> {
                    machineService.resetEngines(machine.getId());
                    return machine;
                })
                .ifPresent(machine -> {
                    machine.setIsActive(isActive.get());
                    machineRepository.save(machine);
                });
    }
}
