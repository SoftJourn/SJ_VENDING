package com.softjourn.vending.service;


import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;

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
    MultipartFile imagePng;
    @Mock
    MultipartFile imageJpg;

    byte[] imageData = new byte[]{1,2,3,4,5,6,7,8,9,12,45,78,56,45,12,5,48,7,54,21,5,45,4,87,8,75,41,21,51};

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
        product.setImageUrl("/products/1/image.jpg");

        updated = new Product();
        updated.setName("Pepsi");
        updated.setPrice(new BigDecimal(10));
        updated.setImageUrl("/products/1/image.jpg");


        when(repository.findOne(1)).thenReturn(product);
        when(repository.findAll()).thenReturn(Collections.singletonList(product));

        when(imagePng.getContentType()).thenReturn("image/png");
        when(imageJpg.getContentType()).thenReturn("image/jpeg");



        when(imagePng.getBytes()).thenReturn(imageData);
        when(imagePng.getBytes()).thenReturn(imageData);
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
        assertEquals("/products/1/image.jpg", newProduct.getImageUrl());
        assertEquals(new BigDecimal(10), newProduct.getPrice());

        verify(repository, times(1)).save(product);
    }

    @Test
    public void testUpdateImage() throws Exception {
        productService.updateImage(imageJpg, 1);
        assertTrue(product.getImageUrl().endsWith("1/image.jpeg"));

        productService.updateImage(imagePng, 1);
        assertTrue(product.getImageUrl().endsWith("1/image.png"));

        assertEquals(product.getImageData(), imageData);

        verify(repository, times(2)).save(product);


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