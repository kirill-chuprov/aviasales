package com.aviasales.task.repository.domain.entity

import kotlin.Double.Companion

data class Location(
  val lat: Double = Double.MIN_VALUE,
  val lon: Double = Companion.MIN_VALUE
)