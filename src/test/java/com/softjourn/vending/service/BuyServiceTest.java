package com.softjourn.vending.service;

import com.softjourn.vending.dto.ProductDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private BuyService buyService;

    private Field field;
    private Field field1;
    private Field field2;
    private Field field3;

    private Row row;
    private Row row1;

    @Before
    public void setUp() throws Exception {

        Product product = new Product();
        product.setId(0);
        product.setName("COLA");
        product.setPrice(new BigDecimal(5));
        product.setImageUrl("/image.jpg");


        Product product2 = new Product();
        product2.setId(1);
        product2.setName("COCA");
        product2.setPrice(new BigDecimal(50));
        product2.setImageUrl("/image2.jpg");

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

        when(vendingService.get(anyInt())).thenReturn(vendingMachine);

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



}