package com.aviasales.task.utils.common

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Kirill Chuprov on 5/15/19.
 */
private const val RADIUS = 6371009.0

class LatLngControlPoints(val firstLatLngControlPoint: LatLng, val secondLatLngControlPoint: LatLng)

fun computePoints(fromX: LatLng, toY: LatLng): List<LatLng> {

  val points = mutableListOf<LatLng>()

  val latLngControlPoints = computeCurveControlPoints(0.5, fromX, toY)

  var step = 0.0

  while (step < 1.01) {
    val curveLatLng = computeCurvePoints(
      fromX,
      latLngControlPoints.firstLatLngControlPoint,
      latLngControlPoints.secondLatLngControlPoint, toY, step
    )
    points.add(curveLatLng)
    step += 0.01
  }
  return points
}

fun computeCurveControlPoints(alpha: Double, from: LatLng, to: LatLng): LatLngControlPoints {

  val curveTangentAngle = 90 * alpha
  val distanceBetween = distanceBetween(from, to)

  val lineHeadingFromStart = angleBetween(from, to)
  val lineHeadingFromEnd = angleBetween(to, from)

  var controlPointHeading1: Double
  var controlPointHeading2: Double

  controlPointHeading1 = lineHeadingFromStart - curveTangentAngle
  controlPointHeading2 = lineHeadingFromEnd - curveTangentAngle


  if (controlPointHeading1 >= 360) controlPointHeading1 -= 360
  if (controlPointHeading2 >= 360) controlPointHeading2 -= 360


  val pA = latLngOffset(from, distanceBetween / 3, controlPointHeading1)
  val pB = latLngOffset(to, distanceBetween / 3, controlPointHeading2)

  return LatLngControlPoints(pA, pB)
}

fun computeCurvePoints(
  from: LatLng,
  iLatLng1: LatLng,
  iLatLng2: LatLng,
  to: LatLng,
  step: Double
): LatLng {

  val arcLatitude = ((1 - step) * (1 - step) * (1 - step) * from.latitude
    + 3.0 * (1 - step) * (1 - step) * step * iLatLng1.latitude
    + 3.0 * (1 - step) * step * step * iLatLng2.latitude
    + step * step * step * to.latitude)
  val arcLongitude = ((1 - step) * (1 - step) * (1 - step) * from.longitude
    + 3.0 * (1 - step) * (1 - step) * step * iLatLng1.longitude
    + 3.0 * (1 - step) * step * step * iLatLng2.longitude
    + step * step * step * to.longitude)
  return LatLng(arcLatitude, arcLongitude)
}

fun distanceBetween(from: LatLng, to: LatLng): Double {
  val fromLatitude = Math.toRadians(from.latitude)
  val fromLongitude = Math.toRadians(from.longitude)
  val toLatitude = Math.toRadians(to.latitude)
  val toLongitude = Math.toRadians(to.longitude)

  val dLat = toLatitude - fromLatitude
  val dLon = toLongitude - fromLongitude
  val a =
    Math.pow(Math.sin(dLat * 0.5), 2.0) + Math.cos(fromLatitude) * Math.cos(toLatitude) * Math.pow(
      Math.sin(dLon * 0.5),
      2.0
    )
  val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return RADIUS * c
}

fun angleBetween(from: LatLng, to: LatLng): Double {
  var angle = Math.toDegrees(
    Math.atan2(
      to.longitude - from.longitude,
      to.latitude - from.latitude
    )
  )
  if (angle < 0) angle += 360

  return angle
}

fun latLngOffset(from: LatLng, distance: Double, heading: Double): LatLng {

  val distanceDivided = distance / RADIUS
  val headingInRadians = Math.toRadians(heading)
  val fromLatitude = Math.toRadians(from.latitude)
  val fromLongitude = Math.toRadians(from.longitude)

  val newLng = Math.asin(
    Math.sin(fromLatitude) * Math.cos(distanceDivided) + Math.cos(fromLatitude) * Math.sin(
      distanceDivided
    ) * Math.cos(
      headingInRadians
    )
  )

  val newLat = fromLongitude + Math.atan2(
    Math.sin(headingInRadians) * Math.sin(distanceDivided) * Math.cos(fromLatitude),
    Math.cos(distanceDivided) - Math.sin(fromLatitude) * Math.sin(newLng)
  )
  return LatLng(Math.toDegrees(newLng), Math.toDegrees(newLat))
}