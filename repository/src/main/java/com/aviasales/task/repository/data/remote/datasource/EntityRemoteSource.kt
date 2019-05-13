package com.aviasales.task.repository.data.remote.datasource

import com.aviasales.task.repository.data.remote.api.AviasalesApi
import com.aviasales.task.repository.domain.datasource.EntityDataSource
import com.aviasales.task.repository.domain.entity.Entity
import io.reactivex.Completable
import io.reactivex.Observable

class EntityRemoteSource(private val api: AviasalesApi) : EntityDataSource {

  override fun observeEntities(): Observable<List<Entity>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}