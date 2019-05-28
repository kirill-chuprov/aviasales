package com.aviasales.task.utils.common

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.View.MeasureSpec
import android.widget.TextView
import com.jakewharton.rxbinding3.widget.TextViewAfterTextChangeEvent
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit.MILLISECONDS
import kotlin.LazyThreadSafetyMode.NONE

val Int.px: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.dp: Int get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val editTextAfterTextChangeTransformer by lazy(NONE) {
  ObservableTransformer<TextViewAfterTextChangeEvent, String> {
    it.skip(2)
      .map { it.editable.toString() }
      .distinctUntilChanged()
      .debounce(200, MILLISECONDS)
  }
}

fun <T> TextView.debouncedAfterTextChanges(mapper: (String) -> T): Observable<T> =
  afterTextChangeEvents().compose(editTextAfterTextChangeTransformer).map(mapper)

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