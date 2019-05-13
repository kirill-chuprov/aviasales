package com.aviasales.task.repository.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EntityL(
  var id: String = "",
  var data: String = ""
){

  @PrimaryKey(autoGenerate = true)
  var dbId: Int = 0
}