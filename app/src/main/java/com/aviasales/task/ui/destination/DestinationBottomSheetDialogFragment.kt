package com.aviasales.task.ui.destination

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.aviasales.task.R
import com.aviasales.task.databinding.DestinationsBottomSheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * Created by Kirill Chuprov on 5/16/19.
 */

class DestinationBottomSheetDialogFragment : BottomSheetDialogFragment() {
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
    return viewBinding!!.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    savedInstanceState?.let { type = it.getInt("type") }
    type = arguments?.getInt("type", 0) ?: Int.MIN_VALUE
    initRecyclerView()
    val cities = vmChooseDestinationScreen.stateReceived().value?.cities
    (viewBinding!!.rvTowns.adapter as DestinationAdapter).submitList(cities)
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
  }

  companion object {
    fun newInstance(type: Int): DestinationBottomSheetDialogFragment =
      DestinationBottomSheetDialogFragment().apply { arguments = bundleOf("type" to type) }
  }
}