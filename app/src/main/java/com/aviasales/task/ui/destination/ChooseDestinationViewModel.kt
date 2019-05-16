package com.aviasales.task.ui.destination

import com.aviasales.task.ui.destination.ChooseDestinationStateChange.Error
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.HideError
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.Loading
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.Success
import com.aviasales.task.ui.destination.ChooseDestinationStateIntent.GetSampleData
import com.aviasales.task.utils.common.BaseViewModel
import com.aviasales.task.utils.common.startWithAndErrHandleWithIO
import io.reactivex.Observable

class ChooseDestinationViewModel() : BaseViewModel<ChooseDestinationState>() {

  override fun initState(): ChooseDestinationState = ChooseDestinationState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(
      listOf(intentStream.ofType(GetSampleData::class.java)
        .map { Success }
        .startWithAndErrHandleWithIO(Loading) { Observable.just(Error(it), HideError) })
    )

  override fun reduceState(
    previousState: ChooseDestinationState,
    stateChange: Any
  ): ChooseDestinationState =
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