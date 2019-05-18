package com.aviasales.task.ui.destination

import com.aviasales.task.ui.destination.ItemState.ItemCity

const val TYPE_FROM = 1
const val TYPE_TO = 2
const val FROM_LAT = "FROM_LAT"
const val FROM_LNG = "FROM_LNG"
const val TO_LAT = "TO_LAT"
const val TO_LNG = "TO_LNG"
const val TOWN_FROM = "TOWN_FROM"
const val TOWN_TO = "TOWN_TO"

data class ChooseDestinationState(
  val success: Boolean = false, val loading: Boolean = false,
  val cities: List<ItemState.ItemCity> = emptyList(),
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

sealed class ChooseDestinationStateChange {
  class Error(val error: Throwable) : ChooseDestinationStateChange()
  object HideError : ChooseDestinationStateChange()
  object Loading : ChooseDestinationStateChange()
  object ConfigurationChanges : ChooseDestinationStateChange()
  class Success(val cities: List<ItemState.ItemCity>) : ChooseDestinationStateChange()
  class CityToSelected(val city: ItemState.ItemCity) : ChooseDestinationStateChange()
  class CityFromSelected(val city: ItemState.ItemCity) : ChooseDestinationStateChange()
}