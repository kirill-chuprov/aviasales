package com.aviasales.task.ui.destination

import com.aviasales.task.repository.domain.interactors.GetCitiesUseCase
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.CityFromSelected
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.CityToSelected
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.ConfigurationChanges
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.Error
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.HideError
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.Loading
import com.aviasales.task.ui.destination.ChooseDestinationStateChange.Success
import com.aviasales.task.ui.destination.ChooseDestinationStateIntent.SelectCityFrom
import com.aviasales.task.ui.destination.ChooseDestinationStateIntent.SelectCityTo
import com.aviasales.task.ui.destination.ItemState.ItemCity
import com.aviasales.task.utils.common.BaseViewModel
import com.aviasales.task.utils.common.startWithAndErrHandleWithIO
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ChooseDestinationViewModel(private val getCitiesUseCase: GetCitiesUseCase) :
  BaseViewModel<ChooseDestinationState>() {

  internal val eventPublisher: PublishSubject<ChooseDestinationStateIntent> by lazy { PublishSubject.create<ChooseDestinationStateIntent>() }

  override fun initState(): ChooseDestinationState = ChooseDestinationState()

  override fun viewIntents(intentStream: Observable<*>): Observable<Any> =
    Observable.merge(
      listOf(
        intentStream.ofType(ChooseDestinationStateIntent.GetCities::class.java)
          .switchMap {
            if (stateReceived().value == null) {
              getCitiesUseCase.execute(it.term, it.lang)
                .map { cities ->
                  val list = cities.map { it.toPresentation() }
                  Success(list)
                }
                .startWithAndErrHandleWithIO(Loading) { Observable.just(Error(it), HideError) }
            } else Observable.just(ConfigurationChanges)

          },
        intentStream.ofType(SelectCityTo::class.java)
          .map { CityToSelected(it.city) },
        intentStream.ofType(SelectCityFrom::class.java)
          .map { CityFromSelected(it.city) }
      )
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
        cityFrom = if (stateChange.cities.isNotEmpty()) stateChange.cities[0] else ItemCity(),
        cityTo = if (stateChange.cities.isNotEmpty()) stateChange.cities[1] else ItemCity(),
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