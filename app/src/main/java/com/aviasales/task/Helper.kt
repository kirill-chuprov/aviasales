package com.aviasales.task

import android.graphics.Point
import com.google.android.gms.maps.model.LatLng
import java.lang.Math.abs
import java.lang.Math.asin
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.pow
import java.lang.Math.sin
import java.lang.Math.sqrt
import java.lang.Math.toDegrees
import java.lang.Math.toRadians

/**
 * Created by Kirill Chuprov on 5/14/19.
 *
 *
 */

/**
 * The earth's radius, in meters.
 */
private val RADIUS = 6371009.0


class LatLngControlPoints(val firstLatLngControlPoint: LatLng, val secondLatLngControlPoint: LatLng)

fun computePoints(): List<LatLng> {

  val fromX = LatLng(50.7143528,74.0059731)
  val toY = LatLng(44.7143528,88.0059731)

  val points = mutableListOf<LatLng>()


    val source = fromX
    val destination = toY
    val latLngControlPoints =
      computeCurveControlPoints(0.toDouble(), source, destination)

    var step = 0.0

    while (step < 1.005) {
      val curveLatLng = computeCurvePoints(
        source,
        latLngControlPoints.firstLatLngControlPoint,
        latLngControlPoints.secondLatLngControlPoint, destination, step
      )
      points.add(curveLatLng)
      step += 0.005
    }


  return points
}

fun computeCurveControlPoints(alpha: Double, from: LatLng, to: LatLng): LatLngControlPoints {

  val curveTangentAngle = 90 * alpha
  val distanceBetween = distanceBetween(from, to)

  val lineHeadingFromStart = angleBetween(from, to)
  val lineHeadingFromEnd = angleBetween(to, from)

  var controlPointHeading1 = 0.0
  var controlPointHeading2 = 0.0
  if (lineHeadingFromStart == 0.0 && lineHeadingFromEnd == 180.0) {
    controlPointHeading1 = lineHeadingFromStart + curveTangentAngle
    controlPointHeading2 = lineHeadingFromEnd + -curveTangentAngle
  } else if (lineHeadingFromStart == 180.0 && lineHeadingFromEnd == 0.0) {
    controlPointHeading1 = lineHeadingFromStart + -curveTangentAngle
    controlPointHeading2 = lineHeadingFromEnd + curveTangentAngle
  } else if (lineHeadingFromStart > 0 && lineHeadingFromEnd > 180) {
    controlPointHeading1 = lineHeadingFromStart + -curveTangentAngle
    controlPointHeading2 = lineHeadingFromEnd + curveTangentAngle
    if (controlPointHeading2 >= 360) {
      controlPointHeading2 = controlPointHeading2 - 360
    }
  } else if (lineHeadingFromStart > 180 && lineHeadingFromEnd > 0) {
    controlPointHeading1 = lineHeadingFromStart + curveTangentAngle
    controlPointHeading2 = lineHeadingFromEnd + -curveTangentAngle
    if (controlPointHeading1 >= 360) {
      controlPointHeading1 = controlPointHeading1 - 360
    }
  }
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
  val fromLatitude = toRadians(from.latitude)
  val fromLongitude = toRadians(from.longitude)
  val toLatitude = toRadians(to.latitude)
  val toLongitude = toRadians(to.longitude)

  val dLat = toLatitude - fromLatitude
  val dLon = toLongitude - fromLongitude
  val a = pow(sin(dLat * 0.5), 2.0) + cos(fromLatitude) * cos(toLatitude) *
    pow(sin(dLon * 0.5), 2.0)
  val c = 2 * atan2(sqrt(a), sqrt(1 - a))
  return RADIUS * c
}

/**
 * Returns the angle between two LatLongs, in degrees.
 */
fun angleBetween(from: LatLng, to: LatLng): Double {
  var angle = toDegrees(
    atan2(
      to.longitude - from.longitude,
      to.latitude - from.latitude
    )
  )
  if (angle < 0) {
    angle = 360 + angle
  }
  return angle
}

/**
 * Returns the angle between two points, in degrees.
 */
fun angleBetween(from: Point, to: Point): Double {
  var angle = toDegrees(atan2((to.y - from.y).toDouble(), (to.x - from.x).toDouble()))
  if (angle < 0) {
    angle = 360 + angle
  }
  return angle
}

/**
 * Returns the distance between two points.
 */
fun distanceBetween(from: Point, to: Point): Double {
  return sqrt(pow(abs(to.x - from.x).toDouble(), 2.0) + pow(abs(to.y - from.y).toDouble(), 2.0))
}

/**
 * Returns the Point resulting from moving a distance from an origin
 * in the specified heading
 *
 * @param from     The Point from which to start.
 * @param distance The distance to travel.
 * @param heading  The heading in degrees.
 */
fun pixelOffset(from: Point, distance: Double, heading: Double): Point {
  val x = from.x + distance * cos(heading)
  val y = from.y + distance * sin(heading)
  return Point(x.toInt(), y.toInt())
}

fun latLngOffset(from: LatLng, distance: Double, heading: Double): LatLng {

  var distance = distance
  var heading = heading

  distance /= RADIUS
  heading = toRadians(heading)
  val fromLatitude = toRadians(from.latitude)
  val fromLongitude = toRadians(from.longitude)
  val newLng =
    asin(sin(fromLatitude) * cos(distance) + cos(fromLatitude) * sin(distance) * cos(heading))
  val newLat = fromLongitude + atan2(
    sin(heading) * sin(distance) * cos(fromLatitude),
    cos(distance) - sin(fromLatitude) * sin(newLng)
  )
  return LatLng(toDegrees(newLng), toDegrees(newLat))
}