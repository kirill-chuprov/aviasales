package com.aviasales.task.di

import com.aviasales.task.utils.common.ImageBindingAdapter
import org.koin.dsl.module.module
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

val mainModule = module {

}

class BindingComponent : androidx.databinding.DataBindingComponent, KoinComponent {

  private val imgLoader: ImageBindingAdapter by inject()

  override fun getImageBindingAdapter(): ImageBindingAdapter = imgLoader
}