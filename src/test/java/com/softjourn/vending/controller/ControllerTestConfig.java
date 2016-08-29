package com.softjourn.vending.controller;

import com.softjourn.vending.dto.Position;
import com.softjourn.vending.dto.ProductDTO;
import com.softjourn.vending.entity.*;
import com.softjourn.vending.service.*;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.web.WebAppConfiguration;

import java.math.BigDecimal;
import java.security.Principal;
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


    private static Product product2;
    private static Product product3;

    private Field field1;
    private Field field2;
    private Field field3;

    private Row row1;

    static {
        product = new Product();
        product.setId(0);
        product.setName("COLA");
        product.setPrice(new BigDecimal(5));
        product.setImageUrl("/image.jpg");
        product.setCategory(Product.Category.DRINK);
        product.setDescription("Cola with coca.");


        product2 = new Product();
        product2.setId(1);
        product2.setName("Nuts");
        product2.setPrice(new BigDecimal(50));
        product2.setImageUrl("/image2.jpg");
        product2.setCategory(Product.Category.SNACK);
        product2.setDescription("Energy bar with nuts.");

        product3 = new Product();
        product3.setId(1);
        product3.setName("Snickers");
        product3.setPrice(new BigDecimal(50));
        product3.setImageUrl("/image3.jpg");
        product3.setCategory(Product.Category.SNACK);
        product3.setDescription("Energy bar with nuts.");
    }

    @Bean
    public ProductService productService() {
        ProductService productService;

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
    public FavoritesService favoritesService() {
        FavoritesService favoritesService;

        favoritesService = Mockito.mock(FavoritesService.class);
        when(favoritesService.get(anyString())).thenReturn(new ArrayList<Product>() {{
            add(product);
            add(product2);
        }});

        return favoritesService;
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
        ProductDTO productDTO2 = new ProductDTO(product3, new Position(1, 1, "B1"));

        when(buyService.getAvailableProducts(anyInt())).thenReturn(new ArrayList<ProductDTO>() {{
            add(productDTO);
            add(productDTO1);
        }});

        when(buyService.buy(anyInt(), anyInt(), any())).thenReturn(true);
        when(buyService.buy(anyInt(), anyString(), any())).thenReturn(false);
        when(buyService.getBestSellers(anyInt())).thenReturn(new ArrayList<Product>(){{add(product2);add(product);}});
        when(buyService.getByCategory(Product.Category.DRINK, 0)).thenReturn(Collections.singletonList(productDTO));
        when(buyService.getByCategory(Product.Category.SNACK, 0)).thenReturn(new ArrayList<ProductDTO>(){{add(productDTO1);add(productDTO2);}});
        when(buyService.getNew(anyInt())).thenReturn(new ArrayList<Product>(){{add(product2);add(product);}});
        when(buyService.lastPurchases(any(Principal.class), anyInt())).thenReturn(new ArrayList<Product>(){{add(product2);add(product);}});

        return buyService;
    }

    @Bean
    public CoinService coinService() {
        CoinService coinService = Mockito.mock(CoinService.class);

        when(coinService.spent(any(), any())).thenReturn(true);

        return coinService;
    }

}
