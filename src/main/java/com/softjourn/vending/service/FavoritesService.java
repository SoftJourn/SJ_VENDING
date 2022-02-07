package com.softjourn.vending.service;

import static com.softjourn.vending.utils.Constants.SQL_DUPLICATE_ENTRY;

import com.softjourn.vending.dao.FavoritesRepository;
import com.softjourn.vending.entity.Favorite;
import com.softjourn.vending.entity.Product;
import com.softjourn.vending.exceptions.ProductAlreadyInFavoritesException;
import com.softjourn.vending.exceptions.ProductIsNotInFavoritesException;
import com.softjourn.vending.exceptions.ProductNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritesService {

  private final FavoritesRepository favoritesRepository;
  private final ProductService productService;

  public List<Product> get(String user) {
    return favoritesRepository.getByAcount(user).stream()
        .map(Favorite::getProduct)
        .collect(Collectors.toList());
  }

  public Product add(String user, Integer productId) {
    Product product = productService.getProduct(productId);
    if (product == null) {
      throw new ProductNotFoundException("Product with id " + productId + " not found.");
    } else {
      Favorite favorite = new Favorite(user, product);
      try {
        favoritesRepository.save(favorite);
      } catch (DataIntegrityViolationException e) {
        log.warn(e.getMessage());
        if (e.getCause() instanceof ConstraintViolationException) {
          ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
          if (cause.getSQLException().getErrorCode() == SQL_DUPLICATE_ENTRY) {
            throw new ProductAlreadyInFavoritesException(
                String.format("Product with id %d is already in favorites", productId));
          }
        }
      }
      return product;
    }
  }

  @Transactional
  public Product delete(String user, Integer productId) {
    Product product = productService.getProduct(productId);
    if (product == null) {
      throw new ProductNotFoundException("Product with id " + productId + " not found.");
    } else {
      if (favoritesRepository.getByAccountAndProduct(user, productId) == null) {
        throw new ProductIsNotInFavoritesException(
            String.format("Product with id %d was not found in favorites", productId));
      } else {
        favoritesRepository.delete(user, productId);
        return productService.getProduct(productId);
      }
    }
  }
}
