package com.aviasales.task.repository.domain.interactors

import com.aviasales.task.repository.domain.entity.City
import com.aviasales.task.repository.domain.repository.CitiesRepository
import io.reactivex.Observable

class GetCitiesUseCase(private val repository: CitiesRepository) {

  fun execute(city: String, language: String): Observable<List<City>> =
    repository.getCities(city, language)
}
