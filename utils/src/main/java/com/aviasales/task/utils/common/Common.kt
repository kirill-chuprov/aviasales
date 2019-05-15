package com.aviasales.task.utils.common

import android.content.res.Resources
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.annotation.DrawableRes
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import android.R.layout
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.View.MeasureSpec



val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.dp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()

fun changeViewImageResource(imageView: ImageView, @DrawableRes resId: Int) {
  imageView.rotation = 0f
  imageView.animate()
    .rotationBy(360f)
    .setDuration(400)
    .setInterpolator(OvershootInterpolator())
    .start()

  imageView.postDelayed({ imageView.setImageResource(resId) }, 120)
}

inline fun <T, reified R> Observable<T>.startWithAndErrHandleWithIO(
  startWith: R,
  noinline errorHandler: (Throwable) -> Observable<R>
): Observable<Any> =
  this.cast(Any::class.java)
    .startWith(startWith)
    .onErrorResumeNext(errorHandler)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

inline fun <T, reified R> Observable<T>.errHandleWithIO(
  noinline errorHandler: (Throwable) -> Observable<R>
): Observable<Any> =
  this.cast(Any::class.java)
    .onErrorResumeNext(errorHandler)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun getBitmapFromView(view: View): Bitmap {
  view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
  val bitmap = Bitmap.createBitmap(
    view.measuredWidth, view.measuredHeight,
    Bitmap.Config.ARGB_8888
  )
  val canvas = Canvas(bitmap)
  view.layout(0, 0, view.measuredWidth, view.measuredHeight)
  view.draw(canvas)
  return bitmap
}