package com.softjourn.vending.service;


import com.softjourn.vending.dao.FavoritesRepository;
import com.softjourn.vending.dao.ImageRepository;
import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.Image;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.NotImageException;
import com.softjourn.vending.exceptions.ProductNotFoundException;
import com.softjourn.vending.exceptions.WrongImageDimensions;
import com.softjourn.vending.utils.FileUploadUtil;
import com.softjourn.vending.utils.ReflectionMergeUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.softjourn.vending.utils.Constants.IMAGE_DIMENSIONS_MAX_HEIGHT;
import static com.softjourn.vending.utils.Constants.IMAGE_DIMENSIONS_MAX_WIDTH;

@Service
@Slf4j
public class ProductService {

    private FavoritesRepository favoritesRepository;
    private ImageRepository imageRepository;

    private ProductRepository productRepository;

    private ReflectionMergeUtil<Product> mergeUtil;

    @Autowired
    public ProductService(@NonNull ProductRepository productRepository,
                          @NotNull FavoritesRepository favoritesRepository,
                          ImageRepository imageRepository) {
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
    public synchronized Image addProductImage(@NonNull MultipartFile file, Integer productId) throws IOException {
        Image image = saveImage(file, productId);
        updateProductImagesUrl(productId);
        return image;
    }

    @Transactional
    public synchronized void addProductImage(@NonNull MultipartFile files[], Integer productId) throws IOException {
        for (MultipartFile file : files) {
            this.saveImage(file, productId);
        }
        this.updateProductImagesUrl(productId);
    }

    @Transactional
    public synchronized void updateCoverImage(@NonNull MultipartFile file, Integer id) throws IOException {
        validateImage(file);
        Product product = getProduct(id);
        setImage(product, file);
        productRepository.save(product);
    }

    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.getProductByCategory_Name(categoryName);
    }


    @Transactional
    public synchronized Product delete(@NonNull Integer id) {
        Product product = getProduct(id);
        favoritesRepository.deleteByProduct(id);
        productRepository.delete(id);
        return product;
    }

    public byte[] getImageById(Integer productId, Long imageId) {
        Image image = this.imageRepository.findOne(imageId);
        if (image == null)
            throw new IllegalArgumentException("There is no images passed id");
        else {
            if (!productId.equals(image.getProductId()))
                throw new IllegalArgumentException("There is no images passed id");
            return image.getData();
        }
    }

    public void deleteImage(Integer productId, Long imageId) {
        Image image = this.imageRepository.findOne(imageId);
        validateImage(productId, image);
        this.imageRepository.delete(image);
        this.updateProductImagesUrl(productId);
    }

    public void setCoverByImgId(Integer productId, Long imgId) {
        Image image = this.imageRepository.findOne(imgId);
        this.validateImage(productId, image);
    }

    String generateImageUrls(Integer productId) {
        List<Image> productImages = this.imageRepository.findByProductId(productId);
        List<String> urls = productImages.stream()
            .map(image -> image.getId() + "." + image.getResolution())
            .map(file -> "\"products/" + productId + "/images/" + file + "\"")
            .collect(Collectors.toList());
        return urls.toString();
    }

    private void validateImage(@NonNull MultipartFile file) throws IOException {
        this.validateImageMimeType(file);
        this.validateImageDimensions(ImageIO.read(file.getInputStream()));
    }

    private Product updateProductImagesUrl(Integer productId) {
        String urls = this.generateImageUrls(productId);
        Product product = this.getProduct(productId);
        //TODO
//        product.setImageUrls(urls);
        return this.update(productId, product);
    }

    private Product setImage(Product product, MultipartFile image) {
        try {
            String resolution = FileUploadUtil.getResolution(image);
            product.setImageData(image.getBytes());
            product.setImageUrl("products/" + product.getId() + "/image." + resolution);
            return product;
        } catch (IOException e) {
            throw new RuntimeException("Can't save image for product with id " + product.getId(), e);
        }
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

    private Image saveImage(@NonNull MultipartFile file, Integer productId) throws IOException {
        validateImage(file);
        String resolution = FileUploadUtil.getResolution(file);
        Image image = new Image(file.getBytes(), productId, resolution);
        return imageRepository.save(image);
    }

    /**
     * Check if image corresponds to proper product
     *
     * @param productId product associated with image
     * @param image     stored image in db
     * @throws IllegalArgumentException If image doesn't exists in DB or product id does not match
     */
    private void validateImage(Integer productId, Image image) throws IllegalArgumentException {
        String message = "There is no images passed id";
        if (image == null) {
            throw new IllegalArgumentException(message);
        } else {
            if (!productId.equals(image.getProductId()))
                throw new IllegalArgumentException(message);
        }
    }
}
