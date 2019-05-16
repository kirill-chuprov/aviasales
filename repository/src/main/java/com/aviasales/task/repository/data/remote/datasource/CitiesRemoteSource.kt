package com.aviasales.task.repository.data.remote.datasource

import com.aviasales.task.repository.data.remote.api.AviasalesApi
import com.aviasales.task.repository.data.remote.entity.toDomain
import com.aviasales.task.repository.domain.datasource.CitiesDataSource
import com.aviasales.task.repository.domain.entity.City
import io.reactivex.Observable

class CitiesRemoteSource(private val api: AviasalesApi) : CitiesDataSource {

  override fun getCities(city: String, language: String): Observable<List<City>> =
    api.getCities(city, language)
      .map { response -> response.cities?.map { it.toDomain() } }
}