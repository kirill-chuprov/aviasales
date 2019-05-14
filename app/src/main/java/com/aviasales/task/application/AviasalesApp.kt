package com.aviasales.task.application

import android.app.Application
import androidx.databinding.DataBindingUtil
import com.aviasales.task.di.BindingComponent
import com.aviasales.task.di.mainModule
import com.aviasales.task.repository.di.repoModule
import com.aviasales.task.utils.di.utilsModule
import org.koin.android.ext.android.startKoin

/**
 * Created by Kirill Chuprov on 5/14/19.
 */

class AviasalesApp : Application() {

  override fun onCreate() {
    super.onCreate()
    startKoin(
      this,
      listOf(
        mainModule, utilsModule, repoModule
      )
    )
    DataBindingUtil.setDefaultComponent(BindingComponent())
  }
}