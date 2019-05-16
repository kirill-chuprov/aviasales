package com.aviasales.task.ui.destination

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.aviasales.task.R
import com.aviasales.task.databinding.FragmentChooseDestinationBinding
import com.aviasales.task.utils.common.BaseFragment
import com.aviasales.task.utils.common.BaseView
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseDestinationFragment : BaseFragment<FragmentChooseDestinationBinding>(),
  BaseView<ChooseDestinationState> {

  private val vmChooseDestinationScreen: ChooseDestinationViewModel by viewModel()
  private val eventPublisher: PublishSubject<ChooseDestinationStateIntent> by lazy { vmChooseDestinationScreen.eventPublisher }

  override fun resLayoutId(): Int = R.layout.fragment_choose_destination

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleStates()
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = super.onCreateView(inflater, container, savedInstanceState)
    .also {
      initIntents()
      initClicks()
    }

  override fun initIntents() {
    viewSubscriptions = Observable.merge(
      listOf(
        Observable.just(ChooseDestinationStateIntent.GetCities("mow", "ru")),
        eventPublisher
      )
    ).subscribe(vmChooseDestinationScreen.viewIntentsConsumer())
  }

  @SuppressLint("CheckResult")
  private fun initClicks() {
    viewBinding!!.tvDestinationTo.clicks()
      .subscribe {
        DestinationBottomSheetDialogFragment.newInstance(TYPE_TO)
          .show(childFragmentManager, "ChooseFrom")
      }

    viewBinding!!.tvDestinationFrom.clicks()
      .subscribe {
        DestinationBottomSheetDialogFragment.newInstance(TYPE_FROM)
          .show(childFragmentManager, "ChooseTo")
      }

    viewBinding!!.btnSearch.clicks()
      .subscribe {
        if (vmChooseDestinationScreen.stateReceived().value != null) {
          with(vmChooseDestinationScreen.stateReceived().value) {
            val bundle = bundleOf(
              "fromLat" to this!!.cityFrom.lat,
              "fromLng" to this.cityFrom.lon,
              "toLat" to this.cityTo.lat,
              "toLng" to this.cityTo.lon,
              "townFrom" to this.cityFrom.name,
              "townTo" to this.cityTo.name
            )
            findNavController().navigate(
              R.id.action_chooseDestinationFragment_to_mapFragment, bundle
            )
          }
        }
      }

  }

  override fun handleStates() {
    vmChooseDestinationScreen.stateReceived().observe(this, Observer { state -> render(state) })
  }

  override fun render(state: ChooseDestinationState) {
    viewBinding!!.viewState = state

    with(viewBinding!!.viewState) {
      if (this!!.cities.isNotEmpty()) {
        viewBinding!!.tvDestinationFrom.text = this.cities[0].name
        viewBinding!!.tvDestinationTo.text = this.cities[1].name
      }
    }

  }
}