package com.aviasales.task.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.aviasales.task.computePoints
import com.aviasales.task.ui.map.MapFragmentStateIntent.GetSampleData
import com.aviasales.task.utils.common.BaseFragment
import com.aviasales.task.utils.common.BaseView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.PolylineOptions
import io.reactivex.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Arrays

class MapFragment : BaseFragment<com.aviasales.task.databinding.FragmentMapBinding>(),
  BaseView<MapFragmentState> {

  private val vmMapFragmentScreen: MapFragmentViewModel by viewModel()

  override fun resLayoutId(): Int = com.aviasales.task.R.layout.fragment_map

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
      initNavigationClicks()
      initMap(savedInstanceState)
    }

  override fun onResume() {
    super.onResume()
    viewBinding!!.map.onResume()

  }

  override fun onPause() {
    viewBinding!!.map.onPause()
    super.onPause()
  }

  override fun onStop() {
    super.onStop()
    viewBinding!!.map.onStop()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    viewBinding!!.map.onLowMemory()
  }

  override fun initIntents() {
    viewSubscriptions = Observable.merge(
      listOf(
        Observable.just(GetSampleData)
      )
    ).subscribe(vmMapFragmentScreen.viewIntentsConsumer())
  }

  private fun initMap(savedInstanceState: Bundle?) {
    with(viewBinding!!) {
      map.onCreate(savedInstanceState)
      map.getMapAsync {

        val ny = LatLng(40.7143528, -74.0059731)
        it.moveCamera(CameraUpdateFactory.newLatLng(ny))
        val computePoints = computePoints()
        val pattern = Arrays.asList(Dot(), Gap(25f))
        val polylineOptions = PolylineOptions()
        polylineOptions.pattern(pattern)
        computePoints.forEach { polylineOptions.add(it) }
        it.addPolyline(polylineOptions)
      }
    }
  }

  private fun initNavigationClicks() {
    viewBinding!!.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
  }

  override fun handleStates() {
    vmMapFragmentScreen.stateReceived().observe(this, Observer { state -> render(state) })
  }

  override fun render(state: MapFragmentState) {
    viewBinding!!.viewState = state
  }
}