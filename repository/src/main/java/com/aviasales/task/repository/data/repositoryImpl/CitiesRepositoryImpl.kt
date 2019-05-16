package com.aviasales.task.repository.data.repositoryImpl

import com.aviasales.task.repository.domain.datasource.CitiesDataSource
import com.aviasales.task.repository.domain.entity.City
import com.aviasales.task.repository.domain.repository.CitiesRepository
import io.reactivex.Observable

class CitiesRepositoryImpl(
  private val remoteCitiesSource: CitiesDataSource
) : CitiesRepository {

  override fun getCities(city: String, language: String): Observable<List<City>> =
    remoteCitiesSource.getCities(city, language)

}