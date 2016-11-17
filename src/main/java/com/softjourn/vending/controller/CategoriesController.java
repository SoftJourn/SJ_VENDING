package com.softjourn.vending.controller;

import com.softjourn.vending.entity.Category;
import com.softjourn.vending.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/categories", produces = MediaType.APPLICATION_JSON_VALUE)
@PreAuthorize("hasAnyRole('INVENTORY','SUPER_ADMIN')")
public class CategoriesController {

    private final CategoriesService categoriesService;

    @Autowired
    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Category>> getCategories() {
        return new ResponseEntity<>(categoriesService.getAll(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Category> addCategories(@Valid @RequestBody Category category) {
        return new ResponseEntity<>(categoriesService.save(category), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<Category> updateCategories(@Valid @RequestBody Category category) {
        return new ResponseEntity<>(categoriesService.save(category), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        return new ResponseEntity<>(categoriesService.get(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeCategories(@PathVariable Long id) {
        categoriesService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

}
