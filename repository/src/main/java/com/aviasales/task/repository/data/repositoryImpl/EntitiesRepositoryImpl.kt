package com.aviasales.task.repository.data.repositoryImpl

import com.aviasales.task.repository.domain.datasource.EntityDataSource
import com.aviasales.task.repository.domain.entity.Entity
import com.aviasales.task.repository.domain.repository.EntitiesRepository
import io.reactivex.Completable
import io.reactivex.Observable

class EntitiesRepositoryImpl(
  private val localSource: EntityDataSource,
  private val remoteSource: EntityDataSource
) : EntitiesRepository {

  private val entitiesObservable: Observable<List<Entity>> = localSource.observeEntities().share()

    override fun observeEntities(): Observable<List<Entity>> = entitiesObservable

}