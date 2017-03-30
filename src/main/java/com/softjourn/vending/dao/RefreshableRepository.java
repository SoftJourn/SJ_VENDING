package com.softjourn.vending.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface RefreshableRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {

    void refresh(T entity);

}
