package com.aviasales.task.repository.data.remote.entity

import com.bluelinelabs.logansquare.annotation.JsonField
import com.bluelinelabs.logansquare.annotation.JsonObject

@JsonObject
data class EntityR(
  @JsonField(name = ["id"]) var id: String? = "",
  @JsonField(name = ["data"]) var data: String? = ""
)