package com.softjourn.vending.dao;

import com.softjourn.vending.entity.Image;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

  List<Image> findByProductId(Integer id);
  List<Image> findByProductIdAndIsCover(Integer id, boolean isCover);
}
