package com.softjourn.vending.service;

import com.softjourn.vending.dao.ProductImageRepository;
import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.ProductImage;
import com.softjourn.vending.entity.listeners.ListenerConfiguration;
import com.softjourn.vending.TestHelper;
import com.softjourn.vending.exceptions.NoImageException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase
public class ProductImageServiceTest {

    private final String testImageName = "test_image.png";

    private ProductImageService imageService;

    @Autowired
    private ProductImageRepository imageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Value("${image.storage.path}")
    private String imageStoragePath;
    private MockMultipartFile testFile;
    private MockMultipartFile testFile2;
    private int productTestId = 1;

    @Test
    public void add() throws Exception {
        ProductImage image = this.imageService.add(testFile, productTestId);
        assertTrue(this.fileExists(image.getUrl()));
    }

    @Test
    public void addAndDelete() throws Exception {
        // add file
        ProductImage image = this.imageService.add(testFile, productTestId);
        assertTrue(this.fileExists(image.getUrl()));
        // delete
        this.imageService.delete(image.getUrl());
        // triggers actual delete in Test environment (Transactional does not help). Another option to use @Rollback(false)
        this.imageRepository.findAll();
        // check in file system
        assertFalse(this.fileExists(image.getUrl()));
        String uri = this.imageService.formUri(testImageName, productTestId);
        // check in db
        ProductImage stored = this.imageRepository.findProductImageByUrl(uri);
        assertNull(stored);
    }

    @Test
    public void addAndRead() throws Exception {
        ProductImage image = this.imageService.add(testFile, productTestId);
        byte[] imageData = this.imageService.get(image.getUrl());
        assertArrayEquals(testFile.getBytes(), imageData);
        assertArrayEquals(testFile.getBytes(), image.getData());
    }

    @Test
    public void addAndSetCover() throws Exception {
        // add new image
        ProductImage newImage = this.imageService.add(testFile, productTestId);
        assertTrue(this.fileExists(newImage.getUrl()));
        // set cover
        ProductImage image = this.imageService.setCover(newImage.getId() + ".png", productTestId);
        assertNotNull(image);
        assertTrue(image.isCover());
        String uri = this.imageService.formUri(newImage.getId() + ".png", productTestId);
        ProductImage storedImage = this.imageRepository.findProductImageByUrl(uri);
        assertEquals(image, storedImage);
    }

    @Test
    public void add_TwoImagesForTheSameProduct() throws Exception {
        ProductImage firstImage = this.imageService.add(testFile, productTestId);
        ProductImage secondImage = this.imageService.add(testFile2, productTestId);
        this.fileExists(firstImage.getUrl());
        this.fileExists(secondImage.getUrl());
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void add_fileProductDoesNotExist_Exception() throws Exception {
        productTestId = Integer.MAX_VALUE;
        this.imageService.add(testFile, productTestId);
    }

    @Test(expected = NoImageException.class)
    public void deleteImageDoesNotExists_Exception() throws Exception {
        String uri = String.format("/%s/images/%s", this.productTestId, this.testImageName);
        assertFalse(this.fileExists(uri));
        this.imageService.delete(uri);
    }

    @Test
    public void get() throws Exception {
        byte[] image = this.imageService.get(testImageName);
        assertNotNull(image);
        assertArrayEquals(testFile.getBytes(), image);
    }

    @Test(expected = NoSuchFileException.class)
    public void get_UnrealPath_Exception() throws Exception {
        this.imageService.get("UnrealPath");
    }

    @Test
    public void parallelSaving() throws Exception {
        // TODO test parallel saving of products
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            Product product = productRepository.findOne(productTestId);
            try {
                imageService.add(testFile, productTestId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    public void postRemove_image() throws Exception {
        ProductImage image = this.imageService.add(testFile, productTestId);
        this.imageRepository.delete(image);
        // triggers actual delete in Test environment (Transactional does not help). Another option to use @Rollback(false)
        this.imageRepository.findAll();
    }

    @Test
    public void productDeleteCascadeImageDelete() throws Exception {
        // add file
        ProductImage image = this.imageService.add(testFile, productTestId);
        assertTrue(this.fileExists(image.getUrl()));
        // delete product
        productRepository.delete(productTestId);
        // triggers actual delete in Test environment (Transactional does not help). Another option to use @Rollback(false)
        productRepository.findAll();
        // check in db
        assertNull(imageRepository.findOne(image.getId()));
        // check in file system
        assertFalse(this.fileExists(image.getUrl()));

    }

    @Before
    public void setUp() throws Exception {
        // set up image service
        imageService = new ProductImageService(imageRepository, imageStoragePath);
        // create listener configuration for listeners injection
        new ListenerConfiguration(imageService);
        // mock multipart file
        testFile = TestHelper.mockMultipartImageFile(testImageName);
        String testImage2Name = "test_image2.png";
        testFile2 = TestHelper.mockMultipartImageFile(testImage2Name);
        // clean PRODUCTS_FOLDER folder
        cleanProductsFolder();
    }

    @After
    public void tearDown() throws Exception {
        cleanProductsFolder();
    }

    private void cleanProductsFolder() throws IOException {
        String productsFolder = "products";
        Path path = Paths.get(String.format("%s/%s", imageStoragePath, productsFolder));
        FileUtils.deleteDirectory(path.toFile());
    }

    private boolean fileExists(String uri) {
        uri = this.imageService.appendSlashIfNotExists(uri);
        String fullPath = imageStoragePath + uri;
        File file = new File(fullPath);
        return file.exists();
    }

}