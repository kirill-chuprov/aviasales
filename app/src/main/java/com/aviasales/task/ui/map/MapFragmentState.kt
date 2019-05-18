package com.aviasales.task.ui.map

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

data class MapFragmentState(
  val success: Boolean = false,
  val loading: Boolean = false,
  val isAnimationInProgress: Boolean = false,
  val firstRun: Boolean = true,
  val marker: Marker? = null,
  val error: Throwable? = null,
  val points: List<LatLng>? = emptyList(),
  var index: Int = -1,
  var next: Int = 0
)

sealed class MapFragmentStateIntent {
  class CalculatePath(val from: LatLng, val to: LatLng) : MapFragmentStateIntent()
  class AnimateNextPoint(val index: Int, val next: Int, val marker: Marker) :
    MapFragmentStateIntent()

  class PassNewMarker(val marker: Marker) : MapFragmentStateIntent()

  object StopAnimation : MapFragmentStateIntent()
}

sealed class MapFragmentStateChange {
  class Error(val error: Throwable) : MapFragmentStateChange()
  object HideError : MapFragmentStateChange()
  object Loading : MapFragmentStateChange()
  class AnimateNextPoint(val index: Int, val next: Int, val marker: Marker) :
    MapFragmentStateChange()

  object StopAnimation : MapFragmentStateChange()
  object ConfigurationChanges : MapFragmentStateChange()
  class Success(val points: List<LatLng>) : MapFragmentStateChange()
  class PassNewMarker(val marker: Marker) : MapFragmentStateChange()
}