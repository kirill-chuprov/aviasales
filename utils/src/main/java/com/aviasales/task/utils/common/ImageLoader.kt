package com.aviasales.task.utils.common

import android.widget.ImageView

interface ImageLoader {
  fun loadImg(
    iv: ImageView,
    url: String?,
    args: Args
  )

  data class Args(
    val placeholderResId: Int = 0,
    val transformCenterCrop: Boolean = false,
    val transformCircle: Boolean = false,
    val roundedCornersRadiusDp: Int = 0
  )
}