package com.aviasales.task.ui.destination

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.aviasales.task.R
import com.aviasales.task.databinding.FragmentChooseDestinationBinding
import com.aviasales.task.ui.destination.ChooseDestinationStateIntent.GetSampleData
import com.aviasales.task.utils.common.BaseFragment
import com.aviasales.task.utils.common.BaseView
import io.reactivex.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseDestinationFragment : BaseFragment<FragmentChooseDestinationBinding>(),
  BaseView<ChooseDestinationState> {

  private val vmChooseDestinationScreen: ChooseDestinationViewModel by viewModel()

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
    }

  override fun initIntents() {
    viewSubscriptions = Observable.merge(
      listOf(
        Observable.just(GetSampleData)
      )
    ).subscribe(vmChooseDestinationScreen.viewIntentsConsumer())
  }

  override fun handleStates() {
    vmChooseDestinationScreen.stateReceived().observe(this, Observer { state -> render(state) })
  }

  override fun render(state: ChooseDestinationState) {
    viewBinding!!.viewState = state
  }
}