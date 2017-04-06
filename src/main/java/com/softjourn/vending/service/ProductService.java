package com.softjourn.vending.service;


import com.softjourn.vending.dao.FavoritesRepository;
import com.softjourn.vending.dao.ProductImageRepository;
import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.ProductImage;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.NotFoundException;
import com.softjourn.vending.exceptions.NotImageException;
import com.softjourn.vending.exceptions.ProductNotFoundException;
import com.softjourn.vending.exceptions.WrongImageDimensions;
import com.softjourn.vending.utils.FileUploadUtil;
import com.softjourn.vending.utils.ReflectionMergeUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.softjourn.vending.utils.Constants.IMAGE_DIMENSIONS_MAX_HEIGHT;
import static com.softjourn.vending.utils.Constants.IMAGE_DIMENSIONS_MAX_WIDTH;

@Service
@Slf4j
public class ProductService {

    private final static String PRODUCTS_RELATIVE_ENDPOINT = "products";
    private final static String IMAGES_ENDPOINT = "images";

    private FavoritesRepository favoritesRepository;
    private ProductImageRepository imageRepository;

    private ProductRepository productRepository;

    private ReflectionMergeUtil<Product> mergeUtil;

    @Autowired
    public ProductService(@NonNull ProductRepository productRepository,
                          @NotNull FavoritesRepository favoritesRepository,
                          ProductImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.favoritesRepository = favoritesRepository;
        this.imageRepository = imageRepository;

        mergeUtil = ReflectionMergeUtil
            .forClass(Product.class)
            .ignoreField("id")
            .ignoreNull(true)
            .build();
    }

    public List<Product> getProducts() {
        Iterable<Product> res = productRepository.findAll();
        return StreamSupport.stream(res.spliterator(), false)
            .collect(Collectors.toList());
    }

    public List<Product> getProductsByNameThatContain(String name) {
        return productRepository.getProductsByNameThatContain(name);
    }

    public Product getProduct(@NonNull Integer id) {
        Product product = productRepository.findOne(id);
        if (product == null) {
            throw new ProductNotFoundException(String.format("Product with id %d not found.", id));
        }
        return product;
    }


    public synchronized Product add(@NonNull Product product) {
        product.setAddedTime(Instant.now());
        return productRepository.save(product);
    }

    public synchronized Product update(@NonNull Integer id, @NonNull Product product) {
        Product old = getProduct(id);
        Product newProduct = mergeUtil.merge(old, product);
        productRepository.save(newProduct);
        return newProduct;
    }

    @Transactional
    public synchronized void addProductImage(@NonNull MultipartFile files[], Integer productId) throws IOException {
        for (MultipartFile file : files) {
            this.saveImage(file, productId);
        }
    }

    @Transactional
    public synchronized void updateCoverImage(@NonNull MultipartFile file, Integer id) throws IOException {
        Product product = getProduct(id);
        ProductImage image = this.saveImage(file, id, true);
        product.getImageUrls().add(image);
        productRepository.save(product);
    }

    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.getProductByCategory_Name(categoryName);
    }

    @Transactional
    public synchronized void delete(@NonNull Integer id) {
        try {
            favoritesRepository.deleteByProduct(id);
            productRepository.delete(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ProductNotFoundException("Product with id " + id + " not found", e);
        }
    }

    public byte[] getImageById(Integer productId, Long imageId) {
        ProductImage image = this.imageRepository.findOne(imageId);
        if (image == null)
            throw new NotFoundException("There is no images passed id");
        else {
            if (!productId.equals(image.getProductId()))
                throw new NotFoundException("There is no images passed id");
            return image.getData();
        }
    }

    public void deleteImage(Integer productId, Long imageId) {
        ProductImage image = this.imageRepository.findOne(imageId);
        validateImage(productId, image);
        this.imageRepository.delete(image);
    }

    public void setCoverByImgId(Integer productId, Long imgId) {
        ProductImage image = this.imageRepository.findOne(imgId);
        this.validateImage(productId, image);
    }

    @Transactional
    synchronized ProductImage addProductImage(@NonNull MultipartFile file, Integer productId) throws IOException {
        Product product = productRepository.findOne(productId);
        if (product == null)
            throw new ProductNotFoundException(String.format("Product with id %d not found.", productId));
        Set<ProductImage> productImages = product.getImageUrls();
        ProductImage image = saveImage(file, productId);
        productImages.add(image);
        productRepository.save(product);
        return image;
    }

    private void validateImage(@NonNull MultipartFile file) throws IOException {
        this.validateImageMimeType(file);
        this.validateImageDimensions(ImageIO.read(file.getInputStream()));
    }


    private void validateImageMimeType(MultipartFile file) {
        String supportedTypes = "image/(?:jpeg|png|jpg|apng|svg|bmp)";
        if (!file.getContentType().matches(supportedTypes)) {
            throw new NotImageException("File is not image");
        }
    }

    private void validateImageDimensions(BufferedImage image) {
        if (image.getWidth() > IMAGE_DIMENSIONS_MAX_WIDTH || image.getHeight() > IMAGE_DIMENSIONS_MAX_HEIGHT) {
            throw new WrongImageDimensions("Wrong image dimensions");
        }
    }

    private ProductImage saveImage(@NonNull MultipartFile file, Integer productId) throws IOException {
        return this.saveImage(file, productId, false);
    }

    private ProductImage saveImage(@NonNull MultipartFile file, Integer productId, boolean isCover) throws IOException {
        validateImage(file);
        String resolution = FileUploadUtil.getResolution(file);
        if (isCover)
            this.dropCover(productId);
        ProductImage image = new ProductImage(file.getBytes(), productId, resolution);
        image.setCover(isCover);
        ProductImage stored = imageRepository.save(image);
        return imageRepository.save(this.setUrlTo(stored));
    }

    private void dropCover(Integer productId) {
        this.imageRepository.findByProductIdAndIsCover(productId, true)
            .stream()
            .peek(image -> image.setCover(false))
            .forEach(image -> this.imageRepository.save(image));
    }

    private ProductImage setUrlTo(ProductImage image) {
        if (image.getId() == null || image.getProductId() == null || image.getResolution() == null) {
            throw new IllegalArgumentException("Can't form urls due to image or product id is not set");
        } else {
            int productId = image.getProductId();
            long imageId = image.getId();
            String type = image.getResolution();
            String url = PRODUCTS_RELATIVE_ENDPOINT + '/' + productId + '/' + IMAGES_ENDPOINT + '/'
                + imageId + '.' + type;
            image.setUrl(url);
            return image;
        }
    }

    /**
     * Check if image corresponds to proper product
     *
     * @param productId product associated with image
     * @param image     stored image in db
     * @throws IllegalArgumentException If image doesn't exists in DB or product id does not match
     */
    // TODO change to NotFoundException
    private void validateImage(Integer productId, ProductImage image) throws IllegalArgumentException {
        String message = "There is no images passed id";
        if (image == null) {
            throw new IllegalArgumentException(message);
        } else {
            if (!productId.equals(image.getProductId()))
                throw new IllegalArgumentException(message);
        }
    }
}
