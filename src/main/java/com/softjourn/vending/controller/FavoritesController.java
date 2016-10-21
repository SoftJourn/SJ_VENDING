package com.softjourn.vending.controller;

import com.softjourn.vending.entity.Product;
import com.softjourn.vending.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/v1/favorites")
public class FavoritesController {

    private FavoritesService favoritesService;

    @Autowired
    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Product> get(Principal principal) {
        return favoritesService.get(principal.getName());
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.POST)
    public Product add(@PathVariable Integer productId, Principal principal) {
        return favoritesService.add(principal.getName(), productId);
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.DELETE)
    public Product delete(@PathVariable Integer productId, Principal principal) {
        return favoritesService.delete(principal.getName(), productId);
    }

}
