package com.aviasales.task.utils.di

import com.aviasales.task.utils.common.GlideImageLoader
import com.aviasales.task.utils.common.ImageLoader
import com.aviasales.task.utils.common.ImageBindingAdapter
import org.koin.dsl.module.module

val utilsModule = module {

  single<ImageLoader> { GlideImageLoader }
  single { ImageBindingAdapter(get()) }
}