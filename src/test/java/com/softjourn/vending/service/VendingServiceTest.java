package com.softjourn.vending.service;


import com.softjourn.vending.dao.FieldRepository;
import com.softjourn.vending.dao.LoadHistoryRepository;
import com.softjourn.vending.dao.MachineRepository;
import com.softjourn.vending.dao.RowRepository;
import com.softjourn.vending.dto.VendingMachineBuilderDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.exceptions.BadRequestException;
import com.softjourn.vending.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static junit.framework.TestCase.*;
import static org.mockito.Matchers.any;
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
}