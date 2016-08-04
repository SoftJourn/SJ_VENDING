package com.softjourn.vending.controller;

import com.softjourn.vending.dto.Position;
import com.softjourn.vending.dto.ProductDTO;
import com.softjourn.vending.entity.Field;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.Row;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.service.*;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import static com.softjourn.vending.controller.ProductsControllerTest.product;
import static com.softjourn.vending.controller.VendingControllerTest.field;
import static com.softjourn.vending.controller.VendingControllerTest.row;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@Configuration
@ComponentScan(basePackages = { "com.softjourn.vending"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@WebAppConfiguration
public class ControllerTestConfig {


    private Product product2;

    private Field field1;
    private Field field2;
    private Field field3;

    private Row row1;

    @Bean
    public ProductService productService() {
        ProductService productService;

        product = new Product();
        product.setId(0);
        product.setName("COLA");
        product.setPrice(new BigDecimal(5));
        product.setImageUrl("/image.jpg");


        product2 = new Product();
        product2.setId(1);
        product2.setName("COCA");
        product2.setPrice(new BigDecimal(50));
        product2.setImageUrl("/image2.jpg");

        productService = Mockito.mock(ProductService.class);
        when(productService.getProducts()).thenReturn(new ArrayList<Product>() {{
            add(product);
            add(product2);
        }});
        when(productService.getProduct(0)).thenReturn(product);
        when(productService.getProduct(1)).thenReturn(product2);
        when(productService.add(product)).thenReturn(product);
        when(productService.add(product2)).thenReturn(product2);
        when(productService.update(anyInt(), any())).thenReturn(product);
        when(productService.delete(anyInt())).thenReturn(product);

        return productService;
    }

    @Bean
    public VendingService vendingService() {
        field = new Field("A1", 0);
        field1 = new Field("A2", 1);
        field2 = new Field("B1", 0);
        field3 = new Field("B2", 1);

        field.setId(0);
        field1.setId(1);
        field2.setId(2);
        field3.setId(3);

        field.setProduct(product);
        field1.setProduct(product2);
        field2.setProduct(product);
        field3.setProduct(product2);

        field.setCount(5);
        field1.setCount(5);
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

        VendingService vendingService = Mockito.mock(VendingService.class);

        when(vendingService.get(anyInt())).thenReturn(vendingMachine);
        when(vendingService.create(any())).thenReturn(vendingMachine);
        when(vendingService.getAll()).thenReturn(Collections.singletonList(vendingMachine));

        return vendingService;
    }

    @Bean
    public FieldService fieldService() {
        FieldService fieldService = Mockito.mock(FieldService.class);

        when(fieldService.update(anyInt(), any(Field.class), anyInt())).then(AdditionalAnswers.returnsSecondArg());
        when(fieldService.updateFieldsCountInRow(anyInt(), anyInt())).thenReturn(row);

        return fieldService;
    }

    @Bean
    public MachineService machineService() {
        MachineService machineService = Mockito.mock(MachineService.class);

        Mockito.doNothing().when(machineService).bye(anyInt(), anyString());

        return machineService;
    }

    @Bean
    public BuyService buyService() {
        BuyService buyService = Mockito.mock(BuyService.class);

        ProductDTO productDTO = new ProductDTO(product, new Position(0, 0, "A0"));
        ProductDTO productDTO1 = new ProductDTO(product2, new Position(0, 1, "A1"));

        when(buyService.getAvailableProducts(anyInt())).thenReturn(new ArrayList<ProductDTO>() {{
            add(productDTO);
            add(productDTO1);
        }});

        when(buyService.buy(anyInt(), anyInt(), any())).thenReturn(true);
        when(buyService.buy(anyInt(), anyString(), any())).thenReturn(false);

        return buyService;
    }

    @Bean
    public CoinService coinService() {
        CoinService coinService = Mockito.mock(CoinService.class);

        when(coinService.spent(any(), any())).thenReturn(true);

        return coinService;
    }

}
