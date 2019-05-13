package com.aviasales.task.repository.domain.repository

import com.aviasales.task.repository.domain.entity.Entity
import io.reactivex.Observable

interface EntitiesRepository {

  fun observeEntities(): Observable<List<Entity>>

}