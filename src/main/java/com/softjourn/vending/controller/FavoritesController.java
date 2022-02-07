package com.softjourn.vending.controller;

import com.softjourn.vending.entity.Product;
import com.softjourn.vending.service.FavoritesService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("authenticated")
@RequestMapping("/v1/favorites")
public class FavoritesController {

  private final FavoritesService favoritesService;

  @GetMapping
  public List<Product> get(Principal principal) {
    return favoritesService.get(principal.getName());
  }

  @PostMapping("/{productId}")
  public Product add(@PathVariable Integer productId, Principal principal) {
    return favoritesService.add(principal.getName(), productId);
  }

  @DeleteMapping("/{productId}")
  public Product delete(@PathVariable Integer productId, Principal principal) {
    return favoritesService.delete(principal.getName(), productId);
  }
}
