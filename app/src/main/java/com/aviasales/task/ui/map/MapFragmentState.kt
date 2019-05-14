package com.aviasales.task.ui.map

data class MapFragmentState(
  val success: Boolean = false, val loading: Boolean = false,
  val error: Throwable? = null
)

sealed class MapFragmentStateIntent {
  object GetSampleData : MapFragmentStateIntent()
}

sealed class MapFragmentStateChange {
  class Error(val error: Throwable) : MapFragmentStateChange()
  object HideError : MapFragmentStateChange()
  object Loading : MapFragmentStateChange()
  object Success : MapFragmentStateChange()
}