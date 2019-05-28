package com.aviasales.task.ui.destination

import com.aviasales.task.repository.domain.entity.City
import com.aviasales.task.ui.destination.ItemState.ItemCity


data class ChooseDestinationState(
  val success: Boolean = false, val loading: Boolean = false,
  val cities: List<ItemCity> = emptyList(),
  val error: Throwable? = null,
  val cityTo: ItemCity = ItemCity(),
  val cityFrom: ItemCity = ItemCity()
)

sealed class ItemState {
  data class ItemCity(
    val id: Int = Int.MIN_VALUE,
    val name: String = "",
    val fullName: String = "",
    val countryCode: String = "",
    val lat: Double = Double.MIN_VALUE,
    val lon: Double = Double.MIN_VALUE
  ) : ItemState()
}

sealed class ChooseDestinationStateIntent {
  class GetCities(
    val term: String,
    val lang: String
  ) : ChooseDestinationStateIntent()

  class SelectCityFrom(val city: ItemCity) : ChooseDestinationStateIntent()
  class SelectCityTo(val city: ItemCity) : ChooseDestinationStateIntent()
}

fun City.toPresentation() = ItemCity(
  id = id,
  name = city,
  countryCode = countryCode,
  fullName = latinFullName,
  lat = location.lat,
  lon = location.lon
)

sealed class ChooseDestinationStateChange {
  class Error(val error: Throwable) : ChooseDestinationStateChange()
  object HideError : ChooseDestinationStateChange()
  object Loading : ChooseDestinationStateChange()
  object ConfigurationChanges : ChooseDestinationStateChange()
  class Success(val cities: List<ItemCity>) : ChooseDestinationStateChange()
  class CityToSelected(val city: ItemCity) : ChooseDestinationStateChange()
  class CityFromSelected(val city: ItemCity) : ChooseDestinationStateChange()
}