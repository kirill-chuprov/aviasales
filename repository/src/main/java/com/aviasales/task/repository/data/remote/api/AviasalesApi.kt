package com.aviasales.task.repository.data.remote.api

import com.aviasales.task.repository.data.remote.entity.dto.CitiesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface AviasalesApi {

    @GET("autocomplete")
    fun getCities(@Query("term") city: String, @Query("lang") language: String): Observable<CitiesResponse>
}