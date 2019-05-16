package com.aviasales.task.ui.destination

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.aviasales.task.R
import com.aviasales.task.databinding.DestinationsBottomSheetDialogBinding
import com.aviasales.task.ui.destination.ChooseDestinationStateIntent.GoToMaps
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kirill Chuprov on 5/16/19.
 */

class DestinationBottomSheetDialogFragment : BottomSheetDialogFragment() {
  private val vmChooseDestinationScreen: ChooseDestinationViewModel by viewModel()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {

    val viewBinding: DestinationsBottomSheetDialogBinding = DataBindingUtil.inflate(
      LayoutInflater.from(context),
      R.layout.destinations_bottom_sheet_dialog,
      container,
      false
    )
    return viewBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    vmChooseDestinationScreen.stateReceived().value

  }

  companion object {
    fun newInstance(bundle: GoToMaps): DestinationBottomSheetDialogFragment =
      DestinationBottomSheetDialogFragment().apply {
        val args = Bundle()
        args.putBoolean("signupFlow", loginFlow)
        arguments = args
      }
  }
}