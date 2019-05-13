package com.aviasales.task.repository.data.local.db

import android.content.Context
import androidx.room.Room

class AviasalesDbProvider {

  companion object {
    fun createDb(context: Context): AviasalesDb {
      return Room.databaseBuilder(context, AviasalesDb::class.java, "AviasalesDatabase")
        .build()
    }
  }
}