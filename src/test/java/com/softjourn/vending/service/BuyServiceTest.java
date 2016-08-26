package com.softjourn.vending.service;

import com.softjourn.vending.dao.PurchaseRepository;
import com.softjourn.vending.dto.ProductDTO;
import com.softjourn.vending.entity.*;
import com.softjourn.vending.exceptions.NotFoundException;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BuyServiceTest {

    @Mock
    private VendingService vendingService;
    @Mock
    private MachineService machineService;
    @Mock
    private CoinService coinService;
    @Mock
    private FieldService fieldService;
    @Mock
    private PurchaseRepository purchaseRepository;

    @InjectMocks
    private BuyService buyService;

    private Product product;
    private Product product2;

    private Field field;
    private Field field1;
    private Field field2;
    private Field field3;

    private Row row;
    private Row row1;

    @Before
    public void setUp() throws Exception {


        product = new Product();
        product.setId(0);
        product.setName("COLA");
        product.setPrice(new BigDecimal(5));
        product.setImageUrl("/image.jpg");
        product.setAddedTime(Instant.ofEpochMilli(1_000_000));

        product2 = new Product();
        product2.setId(1);
        product2.setName("COCA");
        product2.setPrice(new BigDecimal(50));
        product2.setImageUrl("/image2.jpg");
        product2.setAddedTime(Instant.ofEpochMilli(2_000_000));

        field = new Field("A0", 0);
        field1 = new Field("A1", 1);
        field2 = new Field("B0", 0);
        field3 = new Field("B1", 1);

        field.setId(0);
        field1.setId(1);
        field2.setId(2);
        field3.setId(3);

        field.setProduct(product);
        field1.setProduct(product2);
        field2.setProduct(null);
        field3.setProduct(product2);

        field.setCount(5);
        field1.setCount(0);
        field2.setCount(5);
        field3.setCount(5);

        row = new Row("A");
        row1 = new Row("B");

        row.setId(0);
        row1.setId(1);

        row.setFields(new ArrayList<Field>() {{
            add(field);
            add(field1);
        }});
        row1.setFields(new ArrayList<Field>() {{
            add(field2);
            add(field3);
        }});

        VendingMachine vendingMachine = new VendingMachine();
        vendingMachine.setId(0);
        vendingMachine.setName("Snack machine");
        vendingMachine.setRows(new ArrayList<Row>() {{
            add(row);
            add(row1);
        }});

        Purchase purchase = new Purchase("user", product2, vendingMachine);
        Purchase purchase1 = new Purchase("user", product2, vendingMachine);
        Purchase purchase2 = new Purchase("user", product2, vendingMachine);
        Purchase purchase3 = new Purchase("user", product2, vendingMachine);
        Purchase purchase4 = new Purchase("user", product2, vendingMachine);
        Purchase purchase5 = new Purchase("user", product2, vendingMachine);
        Purchase purchase6 = new Purchase("user", product2, vendingMachine);
        Purchase purchase7 = new Purchase("user", product2, vendingMachine);
        Purchase purchase8 = new Purchase("user", product, vendingMachine);
        Purchase purchase9 = new Purchase("user", product, vendingMachine);
        Purchase purchase10 = new Purchase("user", product, vendingMachine);

        List<Purchase> purchases = new ArrayList<Purchase>(){{
            add(purchase);
            add(purchase1);
            add(purchase2);
            add(purchase3);
            add(purchase4);
            add(purchase5);
            add(purchase6);
            add(purchase7);
            add(purchase8);
            add(purchase9);
            add(purchase10);
        }};

        when(vendingService.get(anyInt())).thenReturn(vendingMachine);

        when(purchaseRepository.getAllByMachineId(anyInt())).thenReturn(purchases);


    }

    @Test
    public void getAvailableProducts() throws Exception {
        List<ProductDTO> res=  buyService.getAvailableProducts(0);
        assertEquals(2, res.size());

        assertEquals(0, res.get(0).getPosition().getRow());
        assertEquals(0, res.get(0).getPosition().getColumn());

        assertEquals(1, res.get(1).getPosition().getRow());
        assertEquals(1, res.get(1).getPosition().getColumn());

        assertEquals("A0", res.get(0).getPosition().getCellName());
        assertEquals("B1", res.get(1).getPosition().getCellName());

        assertTrue(0 == res.get(0).getId());
        assertTrue(1 == res.get(1).getId());

        assertEquals("COLA", res.get(0).getName());
        assertEquals("COCA", res.get(1).getName());
    }

    @Test
    public void buyTest() {
        Principal principal = () -> "user";

        assertTrue(buyService.buy(1, "A0", principal));

        verify(vendingService, times(2)).get(1);
        verify(coinService, times(1)).spent(principal, new BigDecimal(5));
        verify(machineService, times(1)).bye(1, "A0");
        verify(fieldService, times(1)).update(anyInt(), any(Field.class), anyInt());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test(expected = NotFoundException.class)
    public void buyNotExistTest() {
        Principal principal = () -> "user";

        assertFalse(buyService.buy(1, "B0", principal));

        verify(vendingService, times(2)).get(1);
        verify(coinService, times(0)).spent(principal, new BigDecimal(5));
        verify(machineService, times(0)).bye(1, "B0");
        verify(fieldService, times(0)).update(anyInt(), any(Field.class), anyInt());
        verify(purchaseRepository, times(0)).save(any(Purchase.class));
    }

    @Test
    public void getBestSellersTest() {
        assertThat(buyService.getBestSellers(0), IsIterableContainingInOrder.contains(product2, product));
    }

    @Test
    public void getNewTest() {
        assertThat(buyService.getNew(0), IsIterableContainingInOrder.contains(product2, product));
    }

}