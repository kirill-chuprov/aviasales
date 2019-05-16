package com.aviasales.task.repository.data.remote.entity.dto

import com.aviasales.task.repository.data.remote.entity.CityR
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
class CitiesResponse(
  @JsonField(name = ["cities"]) var cities: List<CityR>? = null
)