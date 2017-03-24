package com.softjourn.vending.controller;

import com.softjourn.common.auth.OAuthHelper;
import com.softjourn.vending.dto.*;
import com.softjourn.vending.entity.*;
import com.softjourn.vending.service.*;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import static com.softjourn.vending.controller.VendingControllerTest.field;
import static com.softjourn.vending.controller.VendingControllerTest.row;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@EnableSpringDataWebSupport
public abstract class ControllerTestConfig {

    public static Product product;
    public static Product product4;
    public static Category drinks;
    public static Category snacks;
    public static DashboardDTO dashboard;
    public static PurchaseFilterDTO purchaseFilter;
    public static PurchaseFilterDTO purchaseWrongFilter;
    private static Product product2;
    private static Product product3;
    private static Row row1;

    static {
        dashboard = new DashboardDTO();
        dashboard.setProducts(5L);
        dashboard.setMachines(5L);
        dashboard.setCategories(5L);
        dashboard.setPurchases(5L);

        drinks = new Category(1L, "Drink");
        snacks = new Category(2L, "Snack");

        product = new Product();
        product.setId(0);
        product.setName("COLA");
        product.setPrice(new BigDecimal(5));
        product.setCategory(drinks);
        product.setDescription("Cola with coca.");


        product2 = new Product();
        product2.setId(1);
        product2.setName("Nuts");
        product2.setPrice(new BigDecimal(50));
        product2.setCategory(snacks);
        product2.setDescription("Energy bar with nuts.");

        product3 = new Product();
        product3.setId(1);
        product3.setName("Snickers");
        product3.setPrice(new BigDecimal(50));
        product3.setCategory(snacks);
        product3.setDescription("Energy bar with nuts.");

        product4 = new Product();
        product4.setId(2);
        product4.setName("Super Snickers");
        product4.setPrice(new BigDecimal(50));
        product4.setCategory(snacks);
        product4.setDescription("Energy bar with nuts.");

        row = new Row("A");
        row1 = new Row("B");

        // Zone - Europe/Kiev
        purchaseFilter = new PurchaseFilterDTO(1, "Start-Due", -180, "2016-10-06", "2016-10-08");
        purchaseWrongFilter = new PurchaseFilterDTO(1, "Start-Due", -180, "2016-10-06", "2016-10-05");
    }

    private Field field1;
    private Field field2;
    private Field field3;

    @Bean
    public ProductService productService() {
        ProductService productService;

        productService = Mockito.mock(ProductService.class);
        when(productService.getProducts()).thenReturn(new ArrayList<Product>() {{
            add(product);
            add(product2);
            add(product4);
        }});
        when(productService.getProductsByNameThatContain("Snickers")).thenReturn(new ArrayList<Product>() {{
            add(product3);
            add(product4);
        }});
        when(productService.getProduct(0)).thenReturn(product);
        when(productService.getProduct(1)).thenReturn(product2);
        when(productService.getProduct(2)).thenReturn(product4);
        when(productService.add(product)).thenReturn(product);
        when(productService.add(product2)).thenReturn(product2);
        when(productService.add(product4)).thenReturn(product4);
        when(productService.update(anyInt(), any())).thenReturn(product);
        when(productService.getProductsByCategory(anyString())).thenReturn(Collections.singletonList(product));

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
        vendingMachine.setUrl("https://192.168.0.53");
        vendingMachine.setUniqueId(UUID.randomUUID().toString());
        vendingMachine.setRows(new ArrayList<Row>() {{
            add(row);
            add(row1);
        }});
        vendingMachine.setIsActive(true);
        vendingMachine.setCellLimit(2);

        VendingService vendingService = Mockito.mock(VendingService.class);

        when(vendingService.get(anyInt())).thenReturn(vendingMachine);
        when(vendingService.create(any(), any())).thenReturn(vendingMachine);
        when(vendingService.getAll()).thenReturn(Collections.singletonList(vendingMachine));
        when(vendingService.getAllAvailable()).thenReturn(Collections.singletonList(vendingMachine));

        return vendingService;
    }

    @Bean
    public OAuthHelper oAuthHelper() {
        return mock(OAuthHelper.class);
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

        Mockito.doNothing().when(machineService).buy(anyInt(), anyString());

        return machineService;
    }

    @Bean
    public BuyService buyService() {
        BuyService buyService = Mockito.mock(BuyService.class);

        when(buyService.getAvailableProducts(anyInt())).thenReturn(new ArrayList<Product>() {{
            add(product);
            add(product2);
        }});

        when(buyService.getFeatures(anyInt())).thenReturn(new FeatureDTO(new ArrayList<Integer>() {{
            add(0);
            add(1);
        }}, new ArrayList<Integer>() {{
            add(0);
            add(1);
        }}, new ArrayList<CategoryDTO>() {{
            add(new CategoryDTO(drinks.getName(), new ArrayList<Product>() {{
                add(product);
            }}));
            add(new CategoryDTO(snacks.getName(), new ArrayList<Product>() {{
                add(product2);
            }}));
        }}));

        when(buyService.buy(anyInt(), anyInt(), any())).thenReturn(new BigDecimal(5));
        when(buyService.buy(anyInt(), anyString(), any())).thenReturn(new BigDecimal(5));
        when(buyService.getBestSellers(anyInt())).thenReturn(new ArrayList<Product>() {{
            add(product2);
            add(product);
        }});
        when(buyService.getByCategory(drinks, 0)).thenReturn(Collections.singletonList(product));
        when(buyService.getByCategory(snacks, 0)).thenReturn(new ArrayList<Product>() {{
            add(product);
            add(product2);
        }});
        when(buyService.getNew(anyInt())).thenReturn(new ArrayList<Product>() {{
            add(product2);
            add(product);
        }});
        when(buyService.lastPurchases(any(Principal.class))).thenReturn(new ArrayList<PurchaseProductDto>() {{
            add(new PurchaseProductDto(product, Instant.now()));
            add(new PurchaseProductDto(product2, Instant.now()));
        }});
        when(buyService.getByCategoryName(anyString(), anyInt())).thenReturn(Collections.singletonList(product));

        return buyService;
    }

    @Bean
    public PurchaseService purchaseService() throws ParseException {
        PurchaseService purchaseService = Mockito.mock(PurchaseService.class);

        when(purchaseService.getAllUsingFilter(any(PurchaseFilterDTO.class), any(Pageable.class))).thenReturn(
                new PageImpl<>(new ArrayList<PurchaseDTO>() {{
                    add(new PurchaseDTO("ldap", Instant.now(), product.getName(), product.getPrice()));
                    add(new PurchaseDTO("ldap", Instant.now(), product2.getName(), product2.getPrice()));
                }}));

        return purchaseService;
    }

    @Bean
    public CategoriesService categoriesService() {
        CategoriesService categoriesService = Mockito.mock(CategoriesService.class);
        when(categoriesService.getAll()).thenReturn(new ArrayList<Category>() {{
            add(drinks);
            add(snacks);
        }});
        return categoriesService;
    }

    @Bean
    public DashboardService dashboardService() {
        DashboardService dashboardService = Mockito.mock(DashboardService.class);
        when(dashboardService.getDashboard()).thenReturn(dashboard);
        return dashboardService;
    }

    @Bean
    public CoinService coinService() {
        CoinService coinService = Mockito.mock(CoinService.class);

        when(coinService.spent(any(), any(), anyString())).thenReturn(new TransactionDTO());

        return coinService;
    }

}
