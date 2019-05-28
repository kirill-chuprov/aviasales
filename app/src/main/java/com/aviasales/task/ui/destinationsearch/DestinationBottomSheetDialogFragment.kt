package com.aviasales.task.ui.destinationsearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.aviasales.task.R
import com.aviasales.task.databinding.DestinationsBottomSheetDialogBinding
import com.aviasales.task.ui.destination.ChooseDestinationViewModel
import com.aviasales.task.ui.destinationsearch.DestinationSearchStateIntent.GetCities
import com.aviasales.task.utils.common.BaseView
import com.aviasales.task.utils.common.debouncedAfterTextChanges
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kirill Chuprov on 5/16/19.
 */

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DestinationBottomSheetDialogFragment : BottomSheetDialogFragment(),
  BaseView<DestinationSearchState> {

  private var viewSubscriptions: Disposable? = null
  private val vmDestinationSearchScreen: DestinationSearchViewModel by viewModel()
  private val vmChooseDestinationScreen: ChooseDestinationViewModel by sharedViewModel(from = { parentFragment })
  private var viewBinding: DestinationsBottomSheetDialogBinding? = null
  private val eventPublisher by lazy { vmChooseDestinationScreen.eventPublisher }
  private val homeAdapter by lazy {
    DestinationAdapter(
      eventPublisher,
      type,
      this@DestinationBottomSheetDialogFragment
    )
  }
  private var type: Int = Int.MIN_VALUE
  private var searchString: String = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleStates()
    type = arguments?.getInt("type", 0) ?: Int.MIN_VALUE
    searchString = arguments?.getString("searchString", "").toString()
    savedInstanceState?.let {
      type = it.getInt("type")
      searchString = it.getString("searchString")

    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    viewBinding = DataBindingUtil.inflate(
      LayoutInflater.from(context),
      R.layout.destinations_bottom_sheet_dialog,
      container,
      false
    )

    initIntents()
    return viewBinding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecyclerView()
  }

  private fun initRecyclerView() {
    viewBinding!!.rvTowns.apply {
      layoutManager = LinearLayoutManager(context)
      adapter = homeAdapter
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt("type", type)
    outState.putString("searchString", searchString)
  }

  companion object {
    fun newInstance(type: Int, searchString: String): DestinationBottomSheetDialogFragment =
      DestinationBottomSheetDialogFragment().apply {
        arguments = bundleOf("type" to type, "searchString" to searchString)
      }
  }

  override fun initIntents() {
    viewSubscriptions = Observable.merge(
      listOf(
        // yeah sure it should be Locale for language but I am too lazy around 0.00 am
        Observable.just(GetCities(searchString, "ru")),
        viewBinding!!.etSearch.debouncedAfterTextChanges { GetCities(it, "ru") }

      )
    ).subscribe(vmDestinationSearchScreen.viewIntentsConsumer())
  }

  override fun handleStates() {
    vmDestinationSearchScreen.stateReceived().observe(this, Observer { state -> render(state) })
  }

  override fun render(state: DestinationSearchState) {
    searchString = state.searchString
    (viewBinding!!.rvTowns.adapter as DestinationAdapter).submitList(state.cities)
  }
}