package com.softjourn.vending.service;

import com.softjourn.vending.dao.FavoritesRepository;
import com.softjourn.vending.entity.Favorite;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.ProductIsNotInFavoritesException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FavoritesServiceTest {

    Product product;
    Product product1;
    Product product2;

    String userName = "user";

    Favorite favorite;
    Favorite favorite1;
    Favorite favorite2;


    @Mock
    FavoritesRepository favoritesRepository;

    @Mock
    ProductService productService;

    @InjectMocks
    FavoritesService favoritesService;

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

        product1 = new Product();
        product1.setId(2);
        product1.setName("COCA");
        product1.setPrice(new BigDecimal(50));
        product1.setImageUrl("/image2.jpg");
        product1.setAddedTime(Instant.ofEpochMilli(2_500_000));

        favorite = new Favorite(userName, product);
        favorite1 = new Favorite(userName, product1);
        favorite2 = new Favorite(userName, product2);

        List<Favorite> favorites = new ArrayList<Favorite>() {{
            add(favorite);
            add(favorite1);
            add(favorite2);
        }};

        when(favoritesRepository.getByAcount(anyString())).thenReturn(favorites);

        when(productService.getProduct(0)).thenReturn(product);
        when(productService.getProduct(1)).thenReturn(product1);
        when(productService.getProduct(2)).thenReturn(product2);


    }

    @Test
    public void get() throws Exception {
        assertThat(favoritesService.get("user"), containsInAnyOrder(product, product1, product2));
    }

    @Test
    public void add() throws Exception {
        favoritesService.add(userName, 0);
        verify(favoritesRepository, times(1)).save(favorite);
    }

    @Test(expected = ProductIsNotInFavoritesException.class)
    public void delete() throws Exception {
        favoritesService.delete(userName, 0);
        verify(favoritesRepository, times(1)).delete(userName, 0);
    }

}