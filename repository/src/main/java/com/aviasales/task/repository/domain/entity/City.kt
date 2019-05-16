package com.aviasales.task.repository.domain.entity

data class City(
  val id: Int = Int.MIN_VALUE,
  val latinCity: String = "",
  val countryCode: String = "",
  val latinFullName: String = "",
  val city: String = "",
  val location: Location = Location()
)