package com.softjourn.vending.service;

import static com.softjourn.vending.utils.Constants.IMAGE_DIMENSIONS_MAX_HEIGHT;
import static com.softjourn.vending.utils.Constants.IMAGE_DIMENSIONS_MAX_WIDTH;

import com.softjourn.vending.dao.FavoritesRepository;
import com.softjourn.vending.dao.ImageRepository;
import com.softjourn.vending.dao.ProductRepository;
import com.softjourn.vending.entity.Image;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.NotFoundException;
import com.softjourn.vending.exceptions.NotImageException;
import com.softjourn.vending.exceptions.ProductNotFoundException;
import com.softjourn.vending.exceptions.WrongImageDimensions;
import com.softjourn.vending.utils.FileUploadUtil;
import com.softjourn.vending.utils.ReflectionMergeUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ProductService {

  private static final String PRODUCTS_RELATIVE_ENDPOINT = "products";
  private static final String IMAGES_ENDPOINT = "images";

  private final FavoritesRepository favoritesRepository;
  private final ImageRepository imageRepository;
  private final ProductRepository productRepository;
  private final ReflectionMergeUtil<Product> mergeUtil;

  @Autowired
  public ProductService(@NonNull ProductRepository productRepository,
      @NotNull FavoritesRepository favoritesRepository,
      ImageRepository imageRepository
  ) {
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
    return productRepository.findById(id).orElseThrow(
        () -> new ProductNotFoundException(String.format("Product with id %d not found.", id)));
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
  public synchronized Image addProductImage(
      @NonNull MultipartFile file, Integer productId
  ) throws IOException {
    Product product = productRepository.findById(productId).orElseThrow(
        () -> new ProductNotFoundException(
            String.format("Product with id %d not found.", productId)));
    Set<Image> productImages = product.getImageUrls();
    Image image = saveImage(file, productId);
    productImages.add(image);
    productRepository.save(product);
    return image;
  }

  @Transactional
  public synchronized void addProductImage(
      @NonNull MultipartFile[] files, Integer productId
  ) throws IOException {
    for (MultipartFile file : files) {
      this.saveImage(file, productId);
    }
  }

  @Transactional
  public synchronized void updateCoverImage(
      @NonNull MultipartFile file, Integer id
  ) throws IOException {
    Product product = getProduct(id);
    Image image = this.saveImage(file, id, true);
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
      productRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new ProductNotFoundException("Product with id " + id + " not found", e);
    }
  }

  public byte[] getImageById(Integer productId, Long imageId) {
    String errorMsg = "There is no images passed id";
    Image image = this.imageRepository.findById(imageId)
        .orElseThrow(() -> new NotFoundException(errorMsg));
    if (!productId.equals(image.getProductId())) {
      throw new NotFoundException(errorMsg);
    }
    return image.getData();
  }

  public void deleteImage(Integer productId, Long imageId) {
    Image image = this.imageRepository.findById(imageId)
        .orElseThrow(() -> new NotFoundException("There is no images passed id"));
    validateImage(productId, image);
    this.imageRepository.delete(image);
  }

  public void setCoverByImgId(Integer productId, Long imageId) {
    Image image = this.imageRepository.findById(imageId)
        .orElseThrow(() -> new NotFoundException("There is no images passed id"));
    this.validateImage(productId, image);
  }

  private void validateImage(@NonNull MultipartFile file) {
    this.validateImageMimeType(file);
  }


  private void validateImageMimeType(MultipartFile file) {
    String supportedTypes = "image/(?:jpeg|png|jpg|apng|svg|bmp)";
    String contentType = file.getContentType();
    if (contentType != null &&  !contentType.matches(supportedTypes)) {
      throw new NotImageException("File is not image");
    }
  }

  private void validateImageDimensions(BufferedImage image) {
    if (image.getWidth() > IMAGE_DIMENSIONS_MAX_WIDTH
        || image.getHeight() > IMAGE_DIMENSIONS_MAX_HEIGHT
    ) {
      throw new WrongImageDimensions("Wrong image dimensions");
    }
  }

  private Image saveImage(@NonNull MultipartFile file, Integer productId) throws IOException {
    return this.saveImage(file, productId, false);
  }

  private Image saveImage(
      @NonNull MultipartFile file, Integer productId, boolean isCover
  ) throws IOException {
    validateImage(file);
    String resolution = FileUploadUtil.getResolution(file);
    if (isCover)
      this.dropCover(productId);
    Image image = new Image(file.getBytes(), productId, resolution);
    image.setCover(isCover);
    Image stored = imageRepository.save(image);
    return imageRepository.save(this.setUrlTo(stored));
  }

  private void dropCover(Integer productId) {
    this.imageRepository.findByProductIdAndIsCover(productId, true)
        .forEach(image -> {
          image.setCover(false);
          this.imageRepository.save(image);
        });
  }

  private Image setUrlTo(Image image) {
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
