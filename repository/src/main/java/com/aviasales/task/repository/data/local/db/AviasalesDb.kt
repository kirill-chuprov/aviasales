package com.aviasales.task.repository.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aviasales.task.repository.data.local.entity.EntityL

@Database(entities = [EntityL::class], version = 1, exportSchema = false)
@TypeConverters(DbTypeConverters::class)
abstract class AviasalesDb : RoomDatabase() {

  abstract fun entityDao(): EntityDao

}