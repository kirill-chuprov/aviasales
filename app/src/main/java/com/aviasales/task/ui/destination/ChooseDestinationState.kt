package com.aviasales.task.ui.destination

data class ChooseDestinationState(
  val success: Boolean = false, val loading: Boolean = false,
  val error: Throwable? = null
)

sealed class ItemState {
  data class ItemTown(
    val id: String = "",
    val name: String = "",
    val lat: Double = Double.MIN_VALUE,
    val lon: Double = Double.MIN_VALUE
  ) : ItemState()
}

sealed class ChooseDestinationStateIntent {
  class GoToMaps(
    val townFrom: String,
    val townTo: String,
    val fromLat: Double,
    val fromLng: Double,
    val toLat: Double,
    val toLng: Double
  ) : ChooseDestinationStateIntent()
}

sealed class ChooseDestinationStateChange {
  class Error(val error: Throwable) : ChooseDestinationStateChange()
  object HideError : ChooseDestinationStateChange()
  object Loading : ChooseDestinationStateChange()
  object Success : ChooseDestinationStateChange()
}