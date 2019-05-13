package com.aviasales.task.repository.domain.datasource

import com.aviasales.task.repository.domain.entity.Entity
import io.reactivex.Observable

interface EntityDataSource {
  fun observeEntities(): Observable<List<Entity>>
}
