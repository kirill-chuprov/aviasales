package com.aviasales.task.ui.destinationsearch

import com.aviasales.task.repository.domain.interactors.GetCitiesUseCase
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.CityFromSelected
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.CityToSelected
import com.aviasales.task.ui.destination.toPresentation
import com.aviasales.task.ui.destinationsearch.DestinationSearchStateChange.ConfigurationChanges
import com.aviasales.task.ui.destinationsearch.DestinationSearchStateChange.Error
import com.aviasales.task.ui.destinationsearch.DestinationSearchStateChange.HideError
import com.aviasales.task.ui.destinationsearch.DestinationSearchStateChange.Loading
import com.aviasales.task.ui.destinationsearch.DestinationSearchStateChange.Success
import com.aviasales.task.ui.destinationsearch.DestinationSearchStateIntent.GetCities
import com.aviasales.task.utils.common.BaseViewModel
import com.aviasales.task.utils.common.startWithAndErrHandleWithIO
import io.reactivex.Observable

class DestinationSearchViewModel(private val getCitiesUseCase: GetCitiesUseCase) :
  BaseViewModel<DestinationSearchState>() {

  override fun initState(): DestinationSearchState = DestinationSearchState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(
      listOf(
        intentStream.ofType(GetCities::class.java)
          .switchMap {
            getCitiesUseCase.execute(it.term, it.lang)
              .map { cities ->
                val list = cities.map { it.toPresentation() }
                Success(list, it.term)
              }
              .startWithAndErrHandleWithIO(Loading) { Observable.just(Error(it), HideError) }
          }
      )
    )

  override fun reduceState(
    previousState: DestinationSearchState,
    stateChange: Any
  ): DestinationSearchState =
    when (stateChange) {
      is Loading -> previousState.copy(
        loading = true,
        success = false,
        error = null
      )

      is Success -> previousState.copy(
        loading = false,
        success = true,
        searchString = stateChange.term,
        cities = stateChange.cities,
        error = null
      )

      is ConfigurationChanges -> previousState.copy(
        loading = false,
        success = true,
        cityFrom = previousState.cityFrom,
        cityTo = previousState.cityTo,
        cities = previousState.cities,
        error = null
      )

      is Error -> previousState.copy(
        loading = false,
        success = false,
        error = stateChange.error
      )

      is HideError -> previousState.copy(error = null)
      is CityFromSelected -> previousState.copy(cityFrom = stateChange.city)
      is CityToSelected -> previousState.copy(cityTo = stateChange.city)

      else -> previousState
    }
}