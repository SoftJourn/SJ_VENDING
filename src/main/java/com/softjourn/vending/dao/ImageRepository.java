package com.softjourn.vending.dao;

import com.softjourn.vending.entity.Image;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ImageRepository extends CrudRepository<Image, Long> {

    List<Image> findByProductId(Integer id);
}
