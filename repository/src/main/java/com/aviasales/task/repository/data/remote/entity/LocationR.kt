package com.aviasales.task.repository.data.remote.entity

import com.aviasales.task.repository.domain.entity.Location
import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject
import kotlin.Double.Companion

@JsonObject
data class LocationR(
  @JsonField(name = ["lat"]) var lat: Double? = Double.MIN_VALUE,
  @JsonField(name = ["lon"]) var lon: Double? = Double.MIN_VALUE
)

fun LocationR.toDomain() = Location(
  lat = lat ?: Companion.MIN_VALUE,
  lon = lon ?: Companion.MIN_VALUE
)