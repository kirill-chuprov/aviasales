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
import com.aviasales.task.ui.destinationsearch.DestinationBottomSheetDialogFragment
import com.aviasales.task.ui.destinationsearch.FROM_LAT
import com.aviasales.task.ui.destinationsearch.FROM_LNG
import com.aviasales.task.ui.destinationsearch.TOWN_FROM
import com.aviasales.task.ui.destinationsearch.TOWN_TO
import com.aviasales.task.ui.destinationsearch.TO_LAT
import com.aviasales.task.ui.destinationsearch.TO_LNG
import com.aviasales.task.ui.destinationsearch.TYPE_FROM
import com.aviasales.task.ui.destinationsearch.TYPE_TO
import com.aviasales.task.utils.common.BaseFragment
import com.aviasales.task.utils.common.BaseView
import com.google.android.material.snackbar.Snackbar
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
        Observable.just(ChooseDestinationStateIntent.GetCities("mos", "ru")),
        eventPublisher
      )
    ).subscribe(vmChooseDestinationScreen.viewIntentsConsumer())
  }

  @SuppressLint("CheckResult")
  private fun initClicks() {
    viewBinding!!.tvDestinationTo.clicks()
      .subscribe {
        DestinationBottomSheetDialogFragment.newInstance(
          TYPE_TO,
          viewBinding!!.tvDestinationTo.text.toString()
        )
          .show(childFragmentManager, "ChooseFrom")
      }

    viewBinding!!.tvDestinationFrom.clicks()
      .subscribe {
        DestinationBottomSheetDialogFragment.newInstance(
          TYPE_FROM,
          viewBinding!!.tvDestinationFrom.text.toString()
        )
          .show(childFragmentManager, "ChooseFrom")
      }

    viewBinding!!.btnSearch.clicks()
      .subscribe {
        if (vmChooseDestinationScreen.stateReceived().value != null) {
          with(vmChooseDestinationScreen.stateReceived().value) {
            if (this!!.cityFrom.name != cityTo.name) {
              val bundle = bundleOf(
                FROM_LAT to this.cityFrom.lat,
                FROM_LNG to this.cityFrom.lon,
                TO_LAT to this.cityTo.lat,
                TO_LNG to this.cityTo.lon,
                TOWN_FROM to this.cityFrom.name,
                TOWN_TO to this.cityTo.name
              )
              findNavController().navigate(
                R.id.action_chooseDestinationFragment_to_mapFragment, bundle
              )
            } else Snackbar.make(
              viewBinding!!.root,
              R.string.error_same_town,
              Snackbar.LENGTH_SHORT
            ).show()
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