package com.aviasales.task.ui.map

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.aviasales.task.databinding.MarkerBinding
import com.aviasales.task.ui.map.MapFragmentStateIntent.CalculatePath
import com.aviasales.task.utils.common.BaseFragment
import com.aviasales.task.utils.common.BaseView
import com.aviasales.task.utils.common.computePoints
import com.aviasales.task.utils.common.getBitmapFromView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import io.reactivex.Observable
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Arrays

class MapFragment : BaseFragment<com.aviasales.task.databinding.FragmentMapBinding>(),
  BaseView<MapFragmentState> {

  private val vmMapFragmentScreen: MapFragmentViewModel by viewModel()
  private val handler by lazy { Handler() }

  private val fromLat: Double by lazy { arguments!!.getDouble("fromLat", 0.0) }
  private val fromLng: Double by lazy { arguments!!.getDouble("fromLng", 0.0) }
  private val toLat: Double by lazy { arguments!!.getDouble("toLat", 0.0) }
  private val toLng: Double by lazy { arguments!!.getDouble("toLng", 0.0) }

  private val townFrom: String by lazy { arguments!!.getString("townFrom", "") }
  private val townTo: String by lazy { arguments!!.getString("townTo", "") }

  private val fromCoordinate: LatLng by lazy { LatLng(fromLat, fromLng) }
  private val toCoordinate: LatLng by lazy { LatLng(toLat, toLng) }

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

  private fun initNavigationClicks() {
    viewBinding!!.toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
  }


  override fun initIntents() {
    viewSubscriptions = Observable.merge(
      listOf(
        Observable.just(CalculatePath(fromCoordinate, toCoordinate))
      )
    ).subscribe(vmMapFragmentScreen.viewIntentsConsumer())
  }

  private fun initMap(savedInstanceState: Bundle?) {
    with(viewBinding!!) {
      map.onCreate(savedInstanceState)
      map.getMapAsync { googleMap ->

        val fromX = LatLng(40.802237, -74.126443)
        val toY = LatLng(-26.249035, 26.740388)

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(fromX))

//        val computedPoints = computePoints(fromX, toY)
//        val pattern = Arrays.asList(Dot(), Gap(25f))
//        val polylineOptions = PolylineOptions()
//        polylineOptions.pattern(pattern)
//        computedPoints.forEach { polylineOptions.add(it) }
//        googleMap.addPolyline(polylineOptions)


        val fromMarker = MarkerBinding.inflate(layoutInflater)
        val markerTo = MarkerBinding.inflate(layoutInflater)

        fromMarker.town.text = townFrom
        markerTo.town.text = townTo

        val bitmapFromMarker = getBitmapFromView(fromMarker.root)
        val bitmapToMarker = getBitmapFromView(markerTo.root)

        googleMap.addMarker(MarkerOptions().position(fromX).anchor(0.5f, 1f)).setIcon(BitmapDescriptorFactory.fromBitmap(bitmapFromMarker))
        googleMap.addMarker(MarkerOptions().position(toY).anchor(0.5f, 1f)).setIcon(BitmapDescriptorFactory.fromBitmap(bitmapToMarker))

//        val bitmapFromView = getBitmapFromView(view)
//        val scaledBitmap1 = Bitmap.createScaledBitmap(bitmapFromView, 100, 100, false)
//
//
//        googleMap.addMarker(MarkerOptions().position(fromX).anchor(0.5f, 1f))
//          .setIcon(BitmapDescriptorFactory.fromBitmap(bitmapFromView))
//        googleMap.addMarker(
//          MarkerOptions().position(toY)
//            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//            .anchor(0.5f, 1f)
//        )
//        val plane = googleMap.addMarker(MarkerOptions().flat(true).position(fromX))
//        val drawable = ContextCompat.getDrawable(context!!, R.drawable.ic_plane) as BitmapDrawable
//        val scaledBitmap = Bitmap.createScaledBitmap(drawable.bitmap, 100, 100, false)
//
//        plane.setIcon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))
//
//        animatePlane(computedPoints, plane)

      }
    }
  }

  var index = -1
  var next = 1

  private fun animatePlane(
    computedPoints: List<LatLng>,
    marker: Marker
  ) {
    handler.postDelayed(object : Runnable {
      override fun run() {
        lateinit var startPosition: LatLng
        lateinit var endPosition: LatLng

        if (index < computedPoints.lastIndex) {
          index++
          next = index + 1
        }

        if (index < computedPoints.lastIndex) {
          startPosition = computedPoints[index]
          endPosition = computedPoints[next]
        } else return

        val valueAnimator = ValueAnimator.ofFloat(0.toFloat(), 1.toFloat())
        valueAnimator.duration = 300 // duration 3 second
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { animation ->

          val v = animation.animatedFraction
          val lng = v * endPosition.longitude + (1 - v) * startPosition.longitude
          val lat = v * endPosition.latitude + (1 - v) * startPosition.latitude

          val newPosition = LatLng(lat, lng)
          marker.position = newPosition
          marker.setAnchor(0.5f, 0.5f)
          marker.rotation = SphericalUtil.computeHeading(startPosition, endPosition).toFloat()

        }
        valueAnimator.start()

        if (index != computedPoints.lastIndex) {
          handler.postDelayed(this, 300)
        }
      }
    }, 300)

  }

  override fun handleStates() {
    vmMapFragmentScreen.stateReceived().observe(this, Observer { state -> render(state) })
  }

  override fun render(state: MapFragmentState) {
    viewBinding!!.viewState = state
  }
}