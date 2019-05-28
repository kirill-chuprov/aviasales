package com.aviasales.task.ui.destinationsearch

import com.aviasales.task.ui.destination.ItemState.ItemCity

const val TYPE_FROM = 1
const val TYPE_TO = 2
const val FROM_LAT = "FROM_LAT"
const val FROM_LNG = "FROM_LNG"
const val TO_LAT = "TO_LAT"
const val TO_LNG = "TO_LNG"
const val TOWN_FROM = "TOWN_FROM"
const val TOWN_TO = "TOWN_TO"

data class DestinationSearchState(
  val success: Boolean = false,
  val loading: Boolean = false,
  val searchString: String = "",
  val cities: List<ItemCity> = emptyList(),
  val error: Throwable? = null,
  val cityTo: ItemCity = ItemCity(),
  val cityFrom: ItemCity = ItemCity()
)

sealed class DestinationSearchStateIntent {
  class GetCities(
    val term: String,
    val lang: String
  ) : DestinationSearchStateIntent()
}

sealed class DestinationSearchStateChange {
  class Error(val error: Throwable) : DestinationSearchStateChange()
  object HideError : DestinationSearchStateChange()
  object Loading : DestinationSearchStateChange()
  object ConfigurationChanges : DestinationSearchStateChange()
  class Success(val cities: List<ItemCity>, val term: String) : DestinationSearchStateChange()
}