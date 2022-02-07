package com.softjourn.vending.dao;

import java.io.Serializable;
import javax.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class RefreshableRepositoryImpl<T, ID extends Serializable>
    extends SimpleJpaRepository<T, ID> implements RefreshableRepository<T, ID> {

  private final EntityManager entityManager;

  public RefreshableRepositoryImpl(
      JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager
  ) {
    super(entityInformation, entityManager);
    this.entityManager = entityManager;
  }

  public RefreshableRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
    super(domainClass, entityManager);
    this.entityManager = entityManager;
  }

  @Override
  public void refresh(T entity) {
    entityManager.refresh(entity);
  }
}
