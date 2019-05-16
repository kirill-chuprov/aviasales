package com.aviasales.task.repository.domain.repository

import com.aviasales.task.repository.domain.entity.City
import io.reactivex.Observable

interface CitiesRepository {

  fun getCities(city: String, language: String): Observable<List<City>>

}