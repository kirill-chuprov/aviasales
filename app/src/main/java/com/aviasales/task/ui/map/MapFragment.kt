package com.aviasales.task.ui.map

import android.animation.ValueAnimator
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.aviasales.task.R
import com.aviasales.task.R.layout
import com.aviasales.task.databinding.MarkerBinding
import com.aviasales.task.ui.destinationsearch.FROM_LNG
import com.aviasales.task.ui.destinationsearch.TOWN_FROM
import com.aviasales.task.ui.destinationsearch.TOWN_TO
import com.aviasales.task.ui.destinationsearch.TO_LAT
import com.aviasales.task.ui.destinationsearch.TO_LNG
import com.aviasales.task.ui.map.MapFragmentStateIntent.AnimateNextPoint
import com.aviasales.task.ui.map.MapFragmentStateIntent.CalculatePath
import com.aviasales.task.ui.map.MapFragmentStateIntent.PassNewMarker
import com.aviasales.task.ui.map.MapFragmentStateIntent.StopAnimation
import com.aviasales.task.utils.common.BaseFragment
import com.aviasales.task.utils.common.BaseView
import com.aviasales.task.utils.common.getBitmapFromView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PatternItem
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapFragment : BaseFragment<com.aviasales.task.databinding.FragmentMapBinding>(),
  BaseView<MapFragmentState> {

  private val vmMapFragmentScreen: MapFragmentViewModel by viewModel()
  private val eventPublisher: PublishSubject<MapFragmentStateIntent> by lazy { vmMapFragmentScreen.eventPublisher }

  private var fromLat: Double = 0.0
  private var fromLng: Double = 0.0
  private var toLat: Double = 0.0
  private var toLng: Double = 0.0
  private var townFrom: String = ""
  private var townTo: String = ""

  private val fromCoordinate: LatLng by lazy { LatLng(fromLat, fromLng) }
  private val toCoordinate: LatLng by lazy { LatLng(toLat, toLng) }

  private val grayColor: Int by lazy { ContextCompat.getColor(context!!, R.color.aviasalesPrimary) }
  private val polylinePattern: List<PatternItem> = listOf(Dot(), Gap(25f))

  private lateinit var googleMap: GoogleMap

  private val planeIcon: BitmapDrawable by lazy {
    ContextCompat.getDrawable(
      context!!,
      R.drawable.ic_plane
    ) as BitmapDrawable
  }
  private val scaledPlaneIcon by lazy {
    Bitmap.createScaledBitmap(
      planeIcon.bitmap,
      100,
      100,
      false
    )
  }

  override fun resLayoutId(): Int = layout.fragment_map

  override fun onCreate(savedInstanceState: Bundle?) =
    super.onCreate(savedInstanceState).also {
      arguments?.let {
        fromLat = it.getDouble(FROM_LNG, 0.0)
        fromLng = it.getDouble(FROM_LNG, 0.0)
        toLat = it.getDouble(TO_LAT, 0.0)
        toLng = it.getDouble(TO_LNG, 0.0)
        townFrom = it.getString(TOWN_FROM, "")
        townTo = it.getString(TOWN_TO, "")
      }

      handleStates()
    }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? = super.onCreateView(inflater, container, savedInstanceState)
    .also {
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

  override fun onDestroyView() {
    viewBinding!!.map.onDestroy()
    super.onDestroyView()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    viewBinding!!.map.onLowMemory()
  }

  override fun initIntents() {
    viewSubscriptions = Observable.merge(
      listOf(
        Observable.just(CalculatePath(fromCoordinate, toCoordinate)),
        eventPublisher
      )
    ).subscribe(vmMapFragmentScreen.viewIntentsConsumer())
  }

  private fun initMap(savedInstanceState: Bundle?) {
    with(viewBinding!!) {
      map.onCreate(savedInstanceState)
      map.getMapAsync {

        googleMap = it

        val from = LatLng(fromLat, fromLng)
        val to = LatLng(toLat, toLng)

        val builder = LatLngBounds.Builder()
        builder.include(from)
        builder.include(to)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 17),
          object : GoogleMap.CancelableCallback {
            override fun onCancel() {}
            override fun onFinish() {
              googleMap.animateCamera(CameraUpdateFactory.zoomTo(googleMap.cameraPosition.zoom - 0.3f))
            }
          })

        addStartEndMarkers(from, to)


        initIntents()

        vmMapFragmentScreen.stateReceived().value?.let {
          val state = it
          if (state.isAnimationInProgress) {

            //draw polyline
            drawPolyline(getPolyline(), state)

            //get current and next points of animation
            lateinit var currentPoint: LatLng
            lateinit var nextPoint: LatLng

            state.points?.let {
              val points = it
              currentPoint = LatLng(
                points[state.index].latitude,
                points[state.index].longitude
              )

              nextPoint = LatLng(
                points[state.next].latitude,
                points[state.next].longitude
              )

            }

            //add plane to current point on map
            val plane =
              googleMap.addMarker(MarkerOptions().flat(true).position(currentPoint)).apply {
                setIcon(BitmapDescriptorFactory.fromBitmap(scaledPlaneIcon))

                //rotate plane according to next point
                setAnchor(0.5f, 0.5f)
                rotation = SphericalUtil.computeHeading(currentPoint, nextPoint).toFloat()
              }

            //set new marker to animation process
            eventPublisher.onNext(PassNewMarker(plane))

          } else {
            renderStartPosition(state)
          }
        }
      }
    }
  }

  private fun addStartEndMarkers(from: LatLng, to: LatLng) {

    val fromMarker = MarkerBinding.inflate(layoutInflater)
    val markerTo = MarkerBinding.inflate(layoutInflater)

    with(fromMarker.town) {
      text = townFrom.substring(0, 3)
      alpha = 0.8f
    }

    with(markerTo.town) {
      text = townTo.substring(0, 3)
      alpha = 0.8f
    }

    val bitmapFromMarker = getBitmapFromView(fromMarker.root)
    val bitmapToMarker = getBitmapFromView(markerTo.root)

    with(googleMap) {
      addMarker(MarkerOptions().position(from).anchor(0.5f, 1f))
        .setIcon(BitmapDescriptorFactory.fromBitmap(bitmapFromMarker))
      addMarker(MarkerOptions().position(to).anchor(0.5f, 1f))
        .setIcon(BitmapDescriptorFactory.fromBitmap(bitmapToMarker))
    }

  }

  private fun getPolyline(): PolylineOptions =
    PolylineOptions().apply {
      color(grayColor)
      pattern(polylinePattern)
    }

  private fun drawPolyline(polyline: PolylineOptions, state: MapFragmentState) {
    state.points?.forEach { polyline.add(it) }
    googleMap.addPolyline(polyline)
  }

  private fun animatePlane(
    index: Int,
    next: Int,
    computedPoints: List<LatLng>,
    marker: Marker
  ) {
    lateinit var startPosition: LatLng
    lateinit var endPosition: LatLng

    if (index < computedPoints.lastIndex) {
      startPosition = computedPoints[index]
      endPosition = computedPoints[next]
    } else eventPublisher.onNext(StopAnimation).also { return }

    with(ValueAnimator.ofFloat(0f, 1f)) {
      duration = 30
      interpolator = LinearInterpolator()
      addUpdateListener { animation ->

        val fraction = animation.animatedFraction
        val lng = fraction * endPosition.longitude + (1 - fraction) * startPosition.longitude
        val lat = fraction * endPosition.latitude + (1 - fraction) * startPosition.latitude

        val newPosition = LatLng(lat, lng)

        with(marker) {
          position = newPosition
          setAnchor(0.5f, 0.5f)
          rotation = SphericalUtil.computeHeading(startPosition, endPosition).toFloat()
        }
      }

      doOnEnd {
        if (index != computedPoints.lastIndex) {
          eventPublisher.onNext(AnimateNextPoint(index, next, marker))
        }
      }
      start()
    }
  }

  override fun handleStates() {
    vmMapFragmentScreen.stateReceived().observe(this, Observer { state -> render(state) })
  }

  override fun render(state: MapFragmentState) {
    viewBinding!!.viewState = state

    if (state.success && !state.isAnimationInProgress) {
      viewBinding!!.map.getMapAsync {
        googleMap = it
        val plane = renderStartPosition(state)
        //run animation
        eventPublisher.onNext(AnimateNextPoint(state.index, state.next, plane))
      }
    }

    if (state.isAnimationInProgress) {
      with(state) {
        if (points != null) {
          animatePlane(index, next, points, marker!!)
        }
      }
    }
  }

  private fun renderStartPosition(state: MapFragmentState): Marker {
    //draw polyline
    drawPolyline(getPolyline(), state)

    //get current and next points
    val startPoint = LatLng(fromLat, fromLng)
    val nextPoint = LatLng(state.points?.get(1)!!.longitude, state.points[1].latitude)

    //add markerForAnimation
    return googleMap.addMarker(MarkerOptions().flat(true).position(startPoint)).apply {
      setIcon(BitmapDescriptorFactory.fromBitmap(scaledPlaneIcon))
      setAnchor(0.5f, 0.5f)
      rotation = SphericalUtil.computeHeading(startPoint, nextPoint).toFloat()
    }
  }
}