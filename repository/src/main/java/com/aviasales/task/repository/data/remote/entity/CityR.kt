package com.aviasales.task.repository.data.remote.entity

import com.aviasales.task.repository.domain.entity.City
import com.aviasales.task.repository.domain.entity.Location
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
data class CityR(
  @JsonField(name = ["id"]) var id: Int? = Int.MIN_VALUE,
  @JsonField(name = ["latinCity"]) var latinCity: String? = "",
  @JsonField(name = ["countryCode"]) var countryCode: String? = "",
  @JsonField(name = ["latinFullName"]) var latinFullName: String? = "",
  @JsonField(name = ["city"]) var city: String? = "",
  @JsonField(name = ["location"]) var location: LocationR? = LocationR()
)

fun CityR.toDomain() = City(
  id = id ?: Int.MIN_VALUE,
  latinCity = latinCity ?: "",
  countryCode = countryCode ?: "",
  latinFullName = latinFullName ?: "",
  city = city ?: "",
  location = if (location != null) location!!.toDomain() else Location()
)