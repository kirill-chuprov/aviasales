package com.aviasales.task.ui.map

import com.aviasales.task.ui.map.MapFragmentStateChange.Error
import com.aviasales.task.ui.map.MapFragmentStateChange.HideError
import com.aviasales.task.ui.map.MapFragmentStateChange.Loading
import com.aviasales.task.ui.map.MapFragmentStateChange.Success
import com.aviasales.task.ui.map.MapFragmentStateIntent.GetSampleData
import com.aviasales.task.utils.common.BaseViewModel
import com.aviasales.task.utils.common.startWithAndErrHandleWithIO
import io.reactivex.Observable

class MapFragmentViewModel : BaseViewModel<MapFragmentState>() {

  override fun initState(): MapFragmentState = MapFragmentState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(
      listOf(intentStream.ofType(GetSampleData::class.java)
        .map { Success }
        .startWithAndErrHandleWithIO(Loading) { Observable.just(Error(it), HideError) })
    )

  override fun reduceState(previousState: MapFragmentState, stateChange: Any): MapFragmentState =
    when (stateChange) {
      is Loading -> previousState.copy(
        loading = true,
        success = false,
        error = null
      )

      is Success -> previousState.copy(
        loading = false,
        success = true,
        error = null
      )

      is Error -> previousState.copy(
        loading = false,
        success = false,
        error = stateChange.error
      )

      is HideError -> previousState.copy(error = null)

      else -> previousState
    }
}