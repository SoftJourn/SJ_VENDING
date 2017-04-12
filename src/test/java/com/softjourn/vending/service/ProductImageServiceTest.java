package com.softjourn.vending.service;

import com.softjourn.vending.dao.ProductImageRepository;
import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.entity.ProductImage;
import com.softjourn.vending.entity.listeners.ListenerConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import sun.nio.ch.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
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
    private int productTestId = 0;

    @Test
    public void add() throws Exception {
        ProductImage image = this.imageService.add(testFile, productTestId);
        assertTrue(this.fileExists(image.getUrl()));
    }

    @Test
    public void add_TwoImagesForTheSameProduct() throws Exception {
        ProductImage firstImage = this.imageService.add(testFile, productTestId);
        ProductImage secondImage = this.imageService.add(testFile2, productTestId);
        this.fileExists(firstImage.getUrl());
        this.fileExists(secondImage.getUrl());
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void add_Duplicate_Exception() throws Exception {
        this.imageService.add(testFile, productTestId);
        this.imageService.add(testFile, productTestId);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void add_fileProductDoesNotExist_Exception() throws Exception {
        productTestId = Integer.MAX_VALUE;
        this.imageService.add(testFile, productTestId);
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
    public void addAndRead() throws Exception {
        ProductImage image = this.imageService.add(testFile, productTestId);
        byte[] imageData = this.imageService.get(image.getUrl());
        assertArrayEquals(testFile.getBytes(), imageData);
        assertArrayEquals(testFile.getBytes(), image.getData());
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
        // TODO delete from file system when deletes in db
        assertFalse(this.fileExists(image.getUrl()));

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
    public void addAndSetCover() throws Exception {
        this.add();
        ProductImage image = this.imageService.setCover(testImageName, productTestId);
        assertNotNull(image);
        assertTrue(image.isCover());
        String uri = this.imageService.formUri(testImageName, productTestId);
        ProductImage storedImage = this.imageRepository.findProductImageByUrl(uri);
        assertEquals(image, storedImage);
    }

    @Before
    public void setUp() throws Exception {
        // set up image service
        imageService = new ProductImageService(imageRepository, imageStoragePath);
        // create listener configuration for listeners injection
        new ListenerConfiguration(imageService);
        // mock multipart file
        testFile = mockMultipartFile(testImageName);
        String testImage2Name = "test_image2.png";
        testFile2 = mockMultipartFile(testImage2Name);
        // clean PRODUCTS_FOLDER folder
        cleanProductsFolder();
    }

    @After
    public void tearDown() throws Exception {
        cleanProductsFolder();
    }

    private MockMultipartFile mockMultipartFile(String testImageName) throws IOException {
        String passedParameterName = "file";
        String contentType = "image/png";
        String testImagePath;
        testImagePath = "images/".concat(testImageName);
        Resource resource = new ClassPathResource(testImagePath);
        return new MockMultipartFile(passedParameterName, resource.getFilename(), contentType,
            resource.getInputStream());
    }

    private void cleanProductsFolder() throws IOException {
        String productsFolder = "products";
        Path path = Paths.get(String.format("%s/%s", imageStoragePath, productsFolder));
        FileUtils.deleteDirectory(path.toFile());
    }

    private boolean fileExists(String uri) {
        String fullPath = imageStoragePath + uri;
        File file = new File(fullPath);
        return file.exists();
    }

}