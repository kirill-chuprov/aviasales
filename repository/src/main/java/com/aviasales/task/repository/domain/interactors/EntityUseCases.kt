package com.aviasales.task.repository.domain.interactors

import com.aviasales.task.repository.domain.entity.Entity
import com.aviasales.task.repository.domain.repository.EntitiesRepository
import io.reactivex.Observable

class ObserveEntitiesUseCase(private val repository: EntitiesRepository) {

  fun execute(): Observable<List<Entity>> = repository.observeEntities()
}
