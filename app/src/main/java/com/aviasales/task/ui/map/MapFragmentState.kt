package com.aviasales.task.ui.map

import com.google.android.gms.maps.model.LatLng

data class MapFragmentState(
  val success: Boolean = false, val loading: Boolean = false,
  val error: Throwable? = null
)

sealed class MapFragmentStateIntent {
  class CalculatePath(from:LatLng,to:LatLng) : MapFragmentStateIntent()
}

sealed class MapFragmentStateChange {
  class Error(val error: Throwable) : MapFragmentStateChange()
  object HideError : MapFragmentStateChange()
  object Loading : MapFragmentStateChange()
  object Success : MapFragmentStateChange()
}