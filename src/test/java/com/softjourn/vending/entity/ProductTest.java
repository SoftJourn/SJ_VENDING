package com.softjourn.vending.entity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ProductTest {

    Product product;

    @Before
    public void setUp() {
        product = new Product();
        product.setPrices(LocalDateTime.of(2016, 5, 10, 17, 11, 10).toInstant(ZoneOffset.UTC), BigDecimal.valueOf(10));
        product.setPrices(LocalDateTime.of(2016, 5, 10, 17, 11, 11).toInstant(ZoneOffset.UTC), BigDecimal.valueOf(20));
        product.setPrices(LocalDateTime.of(2016, 5, 10, 17, 11, 15).toInstant(ZoneOffset.UTC), BigDecimal.valueOf(30));
        product.setPrices(LocalDateTime.of(2016, 5, 10, 17, 11, 20).toInstant(ZoneOffset.UTC), BigDecimal.valueOf(40));

    }

    @Test
    public void getPricesTest() {
        BigDecimal price = product.getPrice(LocalDateTime.of(2016, 5, 10, 17, 11, 12).toInstant(ZoneOffset.UTC));
        assertEquals(20d, price.doubleValue(), 0);
        assertEquals(40d, product.getPrice().doubleValue(), 0);
    }

}
