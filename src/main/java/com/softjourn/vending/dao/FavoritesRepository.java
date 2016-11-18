package com.softjourn.vending.dao;

import com.softjourn.vending.entity.Favorite;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FavoritesRepository extends CrudRepository<Favorite, Long> {

    @Query("SELECT f FROM Favorite f WHERE f.account = ?1")
    List<Favorite> getByAcount(String account);

    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.account = ?1 AND f.product.id = ?2")
    void delete(String account, Integer productId);

    @Query("SELECT f FROM Favorite f WHERE f.account = ?1 AND f.product.id = ?2")
    Favorite getByAcountAndProduct(String account, Integer productId);

}
