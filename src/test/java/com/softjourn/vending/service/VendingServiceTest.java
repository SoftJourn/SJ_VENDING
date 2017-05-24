package com.softjourn.vending.service;


import com.softjourn.vending.dao.FieldRepository;
import com.softjourn.vending.dao.LoadHistoryRepository;
import com.softjourn.vending.dao.MachineRepository;
import com.softjourn.vending.dao.RowRepository;
import com.softjourn.vending.dto.LoadHistoryRequestDTO;
import com.softjourn.vending.dto.LoadHistoryResponseDTO;
import com.softjourn.vending.dto.PageRequestImpl;
import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.LoadHistory;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.exceptions.BadRequestException;
import com.softjourn.vending.exceptions.NotFoundException;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import static junit.framework.TestCase.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VendingServiceTest {

    @Mock
    OAuth2Authentication principal;
    @Mock
    OAuth2AuthenticationDetails authDetails;
    @InjectMocks
    VendingService service;
    @Captor
    ArgumentCaptor<VendingMachine> machineCaptor;
    private VendingMachineBuilderDTO builder1;
    private VendingMachineBuilderDTO builder2;
    @Mock
    private MachineRepository machineRepository;
    @Mock
    private RowRepository rowRepository;
    @Mock
    private FieldRepository fieldRepository;
    @Mock
    private LoadHistoryRepository loadHistoryRepository;
    @Mock
    private CoinService coinService;

    @Before
    public void setUp() throws Exception {
        when(principal.getDetails()).thenReturn(authDetails);

        builder1 = new VendingMachineBuilderDTO();
        builder1.setColumnsCount(6);
        builder1.setColumnsNumbering(VendingMachineBuilderDTO.Numbering.NUMERICAL);
        builder1.setRowsCount(10);
        builder1.setRowsNumbering(VendingMachineBuilderDTO.Numbering.ALPHABETICAL);
        builder1.setCellLimit(5);
        builder1.setIsActive(true);
        builder1.setName("Machine1");

        builder2 = new VendingMachineBuilderDTO();
        builder2.setColumnsCount(5);
        builder2.setColumnsNumbering(VendingMachineBuilderDTO.Numbering.NUMERICAL);
        builder2.setRowsCount(8);
        builder2.setRowsNumbering(VendingMachineBuilderDTO.Numbering.NUMERICAL);
        builder2.setIsActive(true);
        builder2.setName("Machine2");

        VendingMachine machine = new VendingMachine();

        when(machineRepository.save(any(VendingMachine.class))).thenAnswer(invocation -> {
            VendingMachine vendingMachine = (VendingMachine) invocation.getArguments()[0];
            vendingMachine.setId(1);

            machine.setId(vendingMachine.getId());
            machine.setUrl(vendingMachine.getUrl());
            machine.setRows(vendingMachine.getRows());

            return vendingMachine;
        });
        when(machineRepository.findOne(1)).thenReturn(machine);

        ReflectionTestUtils.setField(service, "coinsServerHost", "http://localhost");
        ReflectionTestUtils.setField(service, "coinRestTemplate", mock(RestTemplate.class));

        VendingMachine machine1 = createMachineWithProducts();
        machine1.setId(2);

        VendingMachine machine2 = createMachineWithProducts();
        machine2.setId(3);

        when(machineRepository.findOne(machine1.getId())).thenReturn(machine1);
        when(machineRepository.findOne(machine2.getId())).thenReturn(null);

        when(loadHistoryRepository.getLoadHistoryByVendingMachine(anyInt(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(prepareLoadHistory()));
        when(loadHistoryRepository.getLoadHistoryByVendingMachineAndTime(anyInt(), any(Instant.class),
                any(Instant.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(prepareLoadHistory()));
    }

    @Test(expected = BadRequestException.class)
    public void refill_notExistedMachine_BadRequestException() throws Exception {
        VendingMachine notExistedMachine = new VendingMachine();
        notExistedMachine.setId(Integer.MAX_VALUE);
        service.refill(notExistedMachine, principal);
    }

    @Test
    public void isProductChanged_notChanged_expectedFalse() {
        Product product1 = new Product();
        product1.setId(1);
        Field field1 = new Field();
        field1.setId(1);
        field1.setInternalId("11");
        field1.setProduct(product1);

        Product product2 = new Product();
        product2.setId(1);
        Field field2 = new Field();
        field2.setId(1);
        field2.setInternalId("11");
        field2.setProduct(product1);

        Row row = new Row("1");
        row.setFields(Collections.singletonList(field1));

        VendingMachine vendingMachine = new VendingMachine();
        vendingMachine.setRows(Collections.singletonList(row));

        assertFalse(service.productChanged(field2, vendingMachine));
    }


    @Test
    public void checkCellLimit_validRefillMachine_true() throws Exception {
        VendingMachine validRefillMachine = new VendingMachine();
        validRefillMachine.setCellLimit(3);
        validRefillMachine.setRows(new ArrayList<Row>() {{
            Row r1 = new Row();
            r1.setFields(new ArrayList<Field>() {{
                Field f1 = new Field();
                f1.setCount(2);
                add(f1);
            }});
            add(r1);
        }});

        assertTrue(VendingService.checkCellLimit(validRefillMachine));
    }

    @Test
    public void checkCellLimit_notValidRefillMachine_false() throws Exception {
        VendingMachine notValidMachine = new VendingMachine();
        notValidMachine.setCellLimit(1);
        notValidMachine.setRows(new ArrayList<Row>() {{
            Row r1 = new Row();
            r1.setFields(new ArrayList<Field>() {{
                Field f1 = new Field();
                f1.setCount(5);
                add(f1);
            }});
            add(r1);
        }});

        assertFalse(VendingService.checkCellLimit(notValidMachine));
    }

    @Test
    public void testCreate() throws Exception {
        VendingMachine machine1 = service.create(builder1, principal);
        VendingMachine machine2 = service.create(builder2, principal);

        assertEquals(60, machine1.getFields().size());
        assertEquals("Machine1", machine1.getName());
        assertEquals(3, machine1.getFields()
                .stream()
                .map(Field::getInternalId)
                .filter(s -> s.equals("A1") || s.equals("C4") || s.equals("J5"))
                .count());

        assertEquals(0, machine1.getFields()
                .stream()
                .map(Field::getInternalId)
                .filter(s -> s.equals("A7") || s.equals("K4"))
                .count());

        assertEquals(40, machine2.getFields().size());
        assertEquals("Machine2", machine2.getName());
        assertEquals(3, machine2.getFields()
                .stream()
                .map(Field::getInternalId)
                .filter(s -> s.equals("11") || s.equals("74") || s.equals("32"))
                .count());

        assertEquals(0, machine2.getFields()
                .stream()
                .map(Field::getInternalId)
                .filter(s -> s.equals("17") || s.equals("94"))
                .count());
    }

    @Test
    public void getLoadedPriceWhenMachinePresentTest() throws Exception {
        BigDecimal price = service.getLoadedPrice(2);
        BigDecimal expectedPrice = new BigDecimal(500);

        assertEquals(expectedPrice, price);
    }

    @Test(expected = NotFoundException.class)
    public void getLoadedPriceWhenMachineNotPresentTest() throws Exception {
        service.getLoadedPrice(3);
    }

    @Test
    public void getLoadHistoryTest() {
        Page<LoadHistory> loadHistory = this.service.getLoadHistory(1, new PageRequest(0, 10));
        assertEquals(5, loadHistory.getContent().size());
    }

    @Test
    public void getLoadHistoryByTimeTest() {
        Page<LoadHistory> loadHistory = this.service.getLoadHistoryByTime(1, Instant.now(),
                Instant.now(), new PageRequest(0, 10));
        assertEquals(5, loadHistory.getContent().size());
    }

    @Test
    public void saveHistoryTest() {
        Product p = new Product();
        p.setId(1);
        p.setName("Cola");
        p.setPrice(BigDecimal.valueOf(5));
        Product p2 = new Product();
        p2.setId(2);
        p2.setName("Soda");
        p2.setPrice(BigDecimal.valueOf(5));
        Product p3 = new Product();
        p3.setId(3);
        p3.setName("Cake");
        p3.setPrice(BigDecimal.valueOf(5));
        VendingMachine oldMachine = service.create(builder1, principal);
        oldMachine.getFields().get(0).setProduct(p);
        oldMachine.getFields().get(0).setCount(2);
        oldMachine.getFields().get(1).setProduct(p2);
        oldMachine.getFields().get(1).setCount(2);

        VendingMachine newMachine = service.create(builder1, principal);
        newMachine.getFields().get(0).setProduct(p);
        newMachine.getFields().get(0).setCount(3);
        newMachine.getFields().get(1).setProduct(p2);
        newMachine.getFields().get(1).setCount(3);
        newMachine.getFields().get(2).setProduct(p3);
        newMachine.getFields().get(2).setCount(1);

        for (int i = 0; i < oldMachine.getFields().size(); i++) {
            oldMachine.getFields().get(i).setId(i + 1);
            newMachine.getFields().get(i).setId(i + 1);
        }

        when(loadHistoryRepository.save(any(List.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(machineRepository.findOne(1)).thenReturn(oldMachine);

        assertEquals(3, service.saveLoadHistory(newMachine, false).size());
    }

    @Test
    public void getLoadHistoryByFilterTest() {
        LoadHistoryRequestDTO requestDTO = new LoadHistoryRequestDTO();
        requestDTO.setMachineId(1);
        requestDTO.setPageable(new PageRequestImpl(10, 0, null));

        assertEquals(5, service.getLoadHistoryByFilter(requestDTO).getContent().size());
        assertEquals(true, service.getLoadHistoryByFilter(requestDTO).getContent().get(0) instanceof LoadHistoryResponseDTO);

        requestDTO.setDue(Instant.now());
        requestDTO.setStart(Instant.now());

        assertEquals(5, service.getLoadHistoryByFilter(requestDTO).getContent().size());
        assertEquals(true, service.getLoadHistoryByFilter(requestDTO).getContent().get(0) instanceof LoadHistoryResponseDTO);
    }

    @Test(timeout = 1000)
    public void exportLoadHistoryTest() throws ReflectiveOperationException {
        LoadHistoryRequestDTO requestDTO = new LoadHistoryRequestDTO();
        requestDTO.setMachineId(1);
        requestDTO.setPageable(new PageRequestImpl(10, 0, null));
        Workbook workbook = service.exportLoadHistory(requestDTO, TimeZone.getTimeZone(ZoneId.of("+3")));

        assertEquals("Load Report", workbook.getSheetName(0));
        assertEquals(10, workbook.getSheet("Load Report").getLastRowNum());
        assertEquals(6, workbook.getSheet("Load Report").getRow(2).getLastCellNum());
    }

    private VendingMachine createMachineWithProducts() {
        Product product1 = new Product();
        product1.setName("Pepsi");
//        product1.setImageUrl("6/image.jpg");
        product1.setPrice(new BigDecimal(100));

        VendingMachine vendingMachine = service.create(builder2, principal);
        vendingMachine.getRows().get(0).getFields().forEach(field -> {
            field.setProduct(product1);
            field.setCount(1);
        });

        return vendingMachine;
    }

    private List<LoadHistory> prepareLoadHistory() {
        List<Instant> instants = new ArrayList<>();
        instants.add(LocalDateTime.of(2016, 5, 10, 17, 11, 10).toInstant(ZoneOffset.UTC));
        instants.add(LocalDateTime.of(2016, 5, 10, 17, 12, 10).toInstant(ZoneOffset.UTC));
        instants.add(LocalDateTime.of(2016, 5, 10, 17, 13, 10).toInstant(ZoneOffset.UTC));
        instants.add(LocalDateTime.of(2016, 5, 10, 17, 14, 10).toInstant(ZoneOffset.UTC));
        instants.add(LocalDateTime.of(2016, 5, 10, 17, 15, 10).toInstant(ZoneOffset.UTC));

        List<LoadHistory> histories = new ArrayList<>();
        Product product = new Product();
        product.setName("Pepsi");
        product.setPrice(new BigDecimal(100));
        Field field = new Field();
        field.setProduct(product);
        VendingMachine machine = new VendingMachine();
        machine.setId(1);
        machine.setUrl("some url");

        for (Instant instant : instants) {
            LoadHistory history = new LoadHistory();
            history.setId(1L);
            history.setField(field);
            history.setVendingMachine(machine);
            history.setProduct(product);
            history.setCount(5);
            history.setPrice(BigDecimal.valueOf(500));
            history.setTotal(BigDecimal.valueOf(500));
            history.setDateAdded(instant);
            history.setHash(UUID.randomUUID().toString());
            histories.add(history);
        }
        return histories;
    }
}