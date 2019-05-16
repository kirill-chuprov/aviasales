package com.aviasales.task.repository.domain.datasource

import com.aviasales.task.repository.domain.entity.City
import io.reactivex.Observable

interface CitiesDataSource {
  fun getCities(city: String, language: String): Observable<List<City>>
}
