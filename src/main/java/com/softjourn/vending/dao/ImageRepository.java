package com.softjourn.vending.dao;

import com.softjourn.vending.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByProductId(Integer id);
    List<Image> findByProductIdAndIsCover(Integer id, boolean isCover);
}
