package com.softjourn.vending.service;


import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import java.math.BigDecimal;
import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

    @Mock
    ProductRepository repository;

    ProductService productService;

    @Mock
    ServletContext servletContext;

    Product product;
    Product updated;


    @Before
    public void setUp() throws Exception {
        productService = new ProductService(repository, servletContext);

        product = new Product();
        product.setId(1);
        product.setName("Cola");
        product.setPrice(new BigDecimal(10));
        product.setImageUrl("/images/1.jpg");

        updated = new Product();
        updated.setName("Pepsi");
        updated.setPrice(new BigDecimal(10));
        updated.setImageUrl("/images/1.jpg");


        when(repository.findOne(1)).thenReturn(product);
        when(repository.findAll()).thenReturn(Collections.singletonList(product));
    }

    @Test
    public void testGetProducts() throws Exception {
        assertTrue(productService.getProducts().contains(product));
        assertEquals(1, productService.getProducts().size());
    }

    @Test(expected = NotFoundException.class)
    public void testGetProductNotFound() throws Exception {
        assertEquals(product, productService.getProduct(2));
    }

    @Test
    public void testGetProduct() throws Exception {
        assertEquals(product, productService.getProduct(1));
    }

    @Test
    public void testUpdate() throws Exception {
        Product newProduct = productService.update(1, updated);
        assertEquals("Pepsi", newProduct.getName());
        assertEquals("/images/1.jpg", newProduct.getImageUrl());
        assertEquals(new BigDecimal(10), newProduct.getPrice());

        verify(repository, times(1)).save(product);
    }

    @Test
    public void addTest() {
        productService.add(updated);

        verify(repository, times(1)).save(updated);
    }

    @Test
    public void deleteTest() {
        assertEquals(product, productService.delete(1));

        verify(repository, times(1)).delete(1);
    }
}