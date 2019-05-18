package com.aviasales.task.ui.map

import com.aviasales.task.repository.domain.interactors.ComputeBezierPointsUseCase
import com.aviasales.task.ui.map.MapFragmentStateChange.ConfigurationChanges
import com.aviasales.task.ui.map.MapFragmentStateChange.Error
import com.aviasales.task.ui.map.MapFragmentStateChange.HideError
import com.aviasales.task.ui.map.MapFragmentStateChange.Loading
import com.aviasales.task.ui.map.MapFragmentStateChange.PassNewMarker
import com.aviasales.task.ui.map.MapFragmentStateChange.Success
import com.aviasales.task.ui.map.MapFragmentStateIntent.AnimateNextPoint
import com.aviasales.task.ui.map.MapFragmentStateIntent.CalculatePath
import com.aviasales.task.ui.map.MapFragmentStateIntent.StopAnimation
import com.aviasales.task.utils.common.BaseViewModel
import com.aviasales.task.utils.common.startWithAndErrHandleWithIO
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MapFragmentViewModel(val computeBezierPointsUseCase: ComputeBezierPointsUseCase) :
  BaseViewModel<MapFragmentState>() {
  internal val eventPublisher: PublishSubject<MapFragmentStateIntent> by lazy { PublishSubject.create<MapFragmentStateIntent>() }
  override fun initState(): MapFragmentState = MapFragmentState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(
      listOf(intentStream.ofType(CalculatePath::class.java)
        .switchMap {
          if (stateReceived().value == null) {
            computeBezierPointsUseCase.execute(it.from, it.to)
              .map { Success(it) }
              .startWithAndErrHandleWithIO(Loading) { Observable.just(Error(it), HideError) }
          } else Observable.just(ConfigurationChanges)
        }
        ,
        intentStream.ofType(AnimateNextPoint::class.java)
          .map { MapFragmentStateChange.AnimateNextPoint(it.index, it.next, it.marker) },
        intentStream.ofType(StopAnimation::class.java)
          .map { MapFragmentStateChange.StopAnimation },
        intentStream.ofType(MapFragmentStateIntent.PassNewMarker::class.java)
          .map { PassNewMarker(it.marker) }
      )
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
        points = stateChange.points,
        success = true,
        firstRun = false,
        error = null
      )

      is PassNewMarker -> previousState.copy(
        marker = stateChange.marker
      )

      is MapFragmentStateChange.AnimateNextPoint -> previousState.copy(
        isAnimationInProgress = true,
        marker = stateChange.marker,
        next = stateChange.next + 1,
        index = stateChange.index + 1
      )

      is MapFragmentStateChange.StopAnimation -> previousState.copy(
        isAnimationInProgress = false,
        success = false
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