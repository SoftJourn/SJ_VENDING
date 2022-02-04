package com.softjourn.vending.controller;

import com.softjourn.vending.entity.Category;
import com.softjourn.vending.service.CategoriesService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('INVENTORY','SUPER_ADMIN')")
@RequestMapping(value = "/v1/categories", produces = MediaType.APPLICATION_JSON_VALUE)
public class CategoriesController {

  private final CategoriesService categoriesService;

  @GetMapping
  public ResponseEntity<List<Category>> getCategories() {
    return new ResponseEntity<>(categoriesService.getAll(), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<Category> addCategories(@Valid @RequestBody Category category) {
    return new ResponseEntity<>(categoriesService.save(category), HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<Category> updateCategories(@Valid @RequestBody Category category) {
    return new ResponseEntity<>(categoriesService.save(category), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Category> getCategory(@PathVariable Long id) {
    return new ResponseEntity<>(categoriesService.get(id), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> removeCategories(@PathVariable Long id) {
    categoriesService.delete(id);
    return new ResponseEntity(HttpStatus.OK);
  }
}
