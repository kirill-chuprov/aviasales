package com.aviasales.task.repository.data.local.datasource

import com.aviasales.task.repository.data.local.db.EntityDao
import com.aviasales.task.repository.data.local.entity.EntityL
import com.aviasales.task.repository.domain.datasource.EntityDataSource
import com.aviasales.task.repository.domain.entity.Entity
import io.reactivex.Completable
import io.reactivex.Observable

class EntityLocalSource(private val entityDao: EntityDao) : EntityDataSource {

  override fun observeEntities(): Observable<List<Entity>> =
    entityDao.getAll()
      .map { if (it.isEmpty()) emptyList() else it.map { item -> item.toDomain() } }
      .toObservable()

  private fun EntityL.toDomain() =
    Entity(
      id = id,
      data = data
    )

  private fun Entity.toLocal() =
    EntityL(
      id = id,
      data = data
    )
}