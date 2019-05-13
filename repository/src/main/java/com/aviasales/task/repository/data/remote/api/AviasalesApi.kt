package com.aviasales.task.repository.data.remote.api

import com.aviasales.task.repository.data.remote.entity.EntityR
import io.reactivex.Observable
import retrofit2.http.GET

interface AviasalesApi {

  @GET("path")
  fun getEntities(): Observable<List<EntityR>>
}