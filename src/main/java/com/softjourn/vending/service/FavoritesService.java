package com.softjourn.vending.service;

import com.softjourn.vending.dao.FavoritesRepository;
import com.softjourn.vending.entity.Favorite;
import com.softjourn.vending.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritesService {

    private FavoritesRepository favoritesRepository;
    private ProductService productService;

    @Autowired
    public FavoritesService(FavoritesRepository favoritesRepository, ProductService productService) {
        this.favoritesRepository = favoritesRepository;
        this.productService = productService;
    }

    public List<Product> get(String user) {
        return favoritesRepository.getByAcount(user).stream()
                .map(Favorite::getProduct)
                .collect(Collectors.toList());
    }

    public void add(String user, Integer productId) {
        Product product = productService.getProduct(productId);
        Favorite favorite = new Favorite(user, product);
        favoritesRepository.save(favorite);
    }

    @Transactional
    public void delete(String user, Integer productId) {
        favoritesRepository.delete(user, productId);
    }


}
