package com.softjourn.vending.service;

import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.dao.PurchaseRepository;
import com.softjourn.vending.dto.PurchaseProductDto;
import com.softjourn.vending.dto.TransactionDTO;
import com.softjourn.vending.entity.*;
import com.softjourn.vending.exceptions.ProductNotFoundInMachineException;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.softjourn.vending.controller.ControllerTestConfig.drinks;
import static junit.framework.TestCase.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
//import static org.mockito.Mockito.*;

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
    @Mock
    ProductRepository productRepository;

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

    private PurchaseProductDto purchaseProductDto0;
    private PurchaseProductDto purchaseProductDto1;
    private PurchaseProductDto purchaseProductDto2;
    private PurchaseProductDto purchaseProductDto3;
    private PurchaseProductDto purchaseProductDto4;
    private PurchaseProductDto purchaseProductDto5;
    private PurchaseProductDto purchaseProductDto6;
    private PurchaseProductDto purchaseProductDto7;
    private PurchaseProductDto purchaseProductDto8;
    private PurchaseProductDto purchaseProductDto9;

    @Before
    public void setUp() throws Exception {


        product = new Product();
        product.setId(0);
        product.setName("COLA");
        product.setPrice(new BigDecimal(5));
        product.setCategory(drinks);
        product.setAddedTime(Instant.ofEpochMilli(1_000_000));
        when(productRepository.getProductByName(eq("COLA"))).thenReturn(product);

        product2 = new Product();
        product2.setId(1);
        product2.setName("COCA");
        product2.setPrice(new BigDecimal(50));
        product2.setCategory(drinks);
        product2.setAddedTime(Instant.ofEpochMilli(2_000_000));
        when(productRepository.getProductByName(eq("COCA"))).thenReturn(product2);

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
        field1.setCount(5);
        field2.setCount(5);
        field3.setCount(5);

        field.setLoaded(Instant.ofEpochSecond(3));
        field1.setLoaded(Instant.ofEpochSecond(2));
        field2.setLoaded(Instant.ofEpochSecond(1));
        field3.setLoaded(Instant.ofEpochSecond(0));

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
        vendingMachine.setIsActive(true);
        vendingMachine.setId(0);
        vendingMachine.setName("Snack machine");
        vendingMachine.setRows(new ArrayList<Row>() {{
            add(row);
            add(row1);
        }});

        Purchase purchase = new Purchase("user", product2.getName(), product2.getPrice(), vendingMachine, Instant.ofEpochSecond(1_000_000));
        Purchase purchase1 = new Purchase("user", product2.getName(), product2.getPrice(), vendingMachine, Instant.ofEpochSecond(2_000_000));
        Purchase purchase2 = new Purchase("user", product2.getName(), product2.getPrice(), vendingMachine, Instant.ofEpochSecond(3_000_000));
        Purchase purchase3 = new Purchase("user", product2.getName(), product2.getPrice(), vendingMachine, Instant.ofEpochSecond(4_000_000));
        Purchase purchase4 = new Purchase("user", product2.getName(), product2.getPrice(), vendingMachine, Instant.ofEpochSecond(5_000_000));
        Purchase purchase5 = new Purchase("user", product2.getName(), product2.getPrice(), vendingMachine, Instant.ofEpochSecond(6_000_000));
        Purchase purchase6 = new Purchase("user", product2.getName(), product2.getPrice(), vendingMachine, Instant.ofEpochSecond(7_000_000));
        Purchase purchase7 = new Purchase("user", product2.getName(), product2.getPrice(), vendingMachine, Instant.ofEpochSecond(8_000_000));
        Purchase purchase8 = new Purchase("user", product.getName(), product.getPrice(), vendingMachine, Instant.ofEpochSecond(9_000_000));
        Purchase purchase9 = new Purchase("user", product.getName(), product.getPrice(), vendingMachine, Instant.ofEpochSecond(10_000_000));
        Purchase purchase10 = new Purchase("user", product.getName(), product.getPrice(), vendingMachine, Instant.ofEpochSecond(11_000_000));


        purchaseProductDto0 = new PurchaseProductDto(product.getName(), product.getPrice(), Instant.ofEpochSecond(11_000_000));
        purchaseProductDto1 = new PurchaseProductDto(product.getName(), product.getPrice(), Instant.ofEpochSecond(10_000_000));
        purchaseProductDto2 = new PurchaseProductDto(product.getName(), product.getPrice(), Instant.ofEpochSecond(9_000_000));
        purchaseProductDto3 = new PurchaseProductDto(product2.getName(), product2.getPrice(), Instant.ofEpochSecond(8_000_000));
        purchaseProductDto4 = new PurchaseProductDto(product2.getName(), product2.getPrice(), Instant.ofEpochSecond(7_000_000));
        purchaseProductDto5 = new PurchaseProductDto(product2.getName(), product2.getPrice(), Instant.ofEpochSecond(6_000_000));
        purchaseProductDto6 = new PurchaseProductDto(product2.getName(), product2.getPrice(), Instant.ofEpochSecond(5_000_000));
        purchaseProductDto7 = new PurchaseProductDto(product2.getName(), product2.getPrice(), Instant.ofEpochSecond(4_000_000));
        purchaseProductDto8 = new PurchaseProductDto(product2.getName(), product2.getPrice(), Instant.ofEpochSecond(3_000_000));
        purchaseProductDto9 = new PurchaseProductDto(product2.getName(), product2.getPrice(), Instant.ofEpochSecond(2_000_000));

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
        when(purchaseRepository.getAllByUser("user")).thenReturn(purchases);

        TransactionDTO transactionDTO = mock(TransactionDTO.class);
        when(transactionDTO.getRemain()).thenReturn(new BigDecimal(10));

        when(coinService.spent(any(), any(), nullable(String.class))).thenReturn(transactionDTO);


    }

    @Test
    public void getAvailableProducts() throws Exception {
        List<Product> res=  buyService.getAvailableProducts(0);
        assertEquals(2, res.size());

        assertTrue(0 == res.get(0).getId());
        assertTrue(1 == res.get(1).getId());

        assertEquals("COLA", res.get(0).getName());
        assertEquals("COCA", res.get(1).getName());
    }

    @Test
    public void buyTest() {
        Principal principal = () -> "user";

        assertEquals(buyService.buy(1, "A0", principal), new BigDecimal(10));

        verify(vendingService, times(4)).get(1);
        verify(coinService, times(1)).spent(eq(principal), eq(new BigDecimal(5)), nullable(String.class));
        verify(machineService, times(1)).buy(1, "A0");
        verify(fieldService, times(1)).update(anyInt(), any(Field.class), anyInt());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));
    }

    @Test
    public void buyLastItemTest() {
        Principal principal = () -> "user";
        ArgumentCaptor<Field> argumentCaptor = ArgumentCaptor.forClass(Field.class);
        field.setCount(1);

        assertEquals(buyService.buy(1, "A0", principal), new BigDecimal(10));

        verify(vendingService, times(4)).get(1);
        verify(coinService, times(1)).spent(eq(principal), eq(new BigDecimal(5)), nullable(String.class));
        verify(machineService, times(1)).buy(1, "A0");
        verify(fieldService, times(1)).update(anyInt(), argumentCaptor.capture(), anyInt());
        verify(purchaseRepository, times(1)).save(any(Purchase.class));

        assertEquals(0, Math.toIntExact(argumentCaptor.getValue().getCount()));
        assertNull(argumentCaptor.getValue().getProduct());
    }

    @Test(expected = ProductNotFoundInMachineException.class)
    public void buyNotExistTest() {
        Principal principal = () -> "user";

        assertEquals(buyService.buy(1, "B0", principal), new BigDecimal(10));

        verify(vendingService, times(2)).get(1);
        verify(coinService, times(0)).spent(eq(principal), eq(new BigDecimal(5)), nullable(String.class));
        verify(machineService, times(0)).buy(1, "B0");
        verify(fieldService, times(0)).update(anyInt(), any(Field.class), anyInt());
        verify(purchaseRepository, times(0)).save(any(Purchase.class));
    }

    @Test
    public void getBestSellersTest() {
        assertThat(buyService.getBestSellers(0), IsIterableContainingInOrder.contains(product2.getId(), product.getId()));
    }

    @Test
    public void getLastAddedTest() {
        assertThat(buyService.getLastAdded(0), IsIterableContainingInOrder.contains(product.getId(), product2.getId()));
    }

    @Test
    public void getLastPurchasesTest() throws Exception {
        assertThat(buyService.lastPurchases(() -> "user"),  IsIterableContainingInOrder.contains(
                purchaseProductDto0,
                purchaseProductDto1,
                purchaseProductDto2,
                purchaseProductDto3,
                purchaseProductDto4,
                purchaseProductDto5,
                purchaseProductDto6,
                purchaseProductDto7,
                purchaseProductDto8,
                purchaseProductDto9));
    }
}
