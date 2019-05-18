package com.aviasales.task.repository.domain.interactors

import com.aviasales.task.repository.domain.entity.City
import com.aviasales.task.repository.domain.repository.CitiesRepository
import com.aviasales.task.repository.domain.services.GeoService
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable

class GetCitiesUseCase(private val repository: CitiesRepository) {

  fun execute(city: String, language: String): Observable<List<City>> =
    repository.getCities(city, language)
}

class ComputeBezierPointsUseCase(private val geoService: GeoService) {

  fun execute(from: LatLng, to: LatLng): Observable<List<LatLng>> =
    geoService.computeBezierPoints(from, to)
}
