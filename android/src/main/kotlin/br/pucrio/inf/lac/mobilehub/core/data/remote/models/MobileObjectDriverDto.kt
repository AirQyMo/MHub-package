package br.pucrio.inf.lac.mobilehub.core.data.remote.models

import com.google.gson.annotations.SerializedName

data class MobileObjectDriverDto(
    @SerializedName(value = "id") val id: Long,
    @SerializedName(value = "wpan") val wpan: Int,
    @SerializedName(value = "name") var name: String,
    @SerializedName(value = "config") val config: String? = null,
    @SerializedName(value = "content") val content: String
)