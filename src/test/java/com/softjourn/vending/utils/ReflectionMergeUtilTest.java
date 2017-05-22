package com.softjourn.vending.utils;


import com.softjourn.vending.entity.Product;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.TreeMap;

import static junit.framework.TestCase.assertEquals;


public class ReflectionMergeUtilTest {

    private Product product;
    private Product productUpdater;

    @Before
    public void setUp() throws Exception {
        product = new Product();
        product.setId(1);
        product.setName("Cola");
        product.setPrice(new BigDecimal(5));

        productUpdater = new Product();
        productUpdater.setId(2);
        productUpdater.setName("Pepsi");
        productUpdater.setPrice(new BigDecimal(20));

    }

    @Test
    public void testMerge() throws Exception {
        ReflectionMergeUtil<Product> mergeUtil = ReflectionMergeUtil
                .forClass(Product.class)
                .ignoreField("id")
                .ignoreNull(true)
                .build();

        product = mergeUtil.merge(product, productUpdater);

        assertEquals(new Integer(1), product.getId());
        assertEquals("Pepsi", product.getName());
        assertEquals(new BigDecimal(20), product.getPrice());
    }
}