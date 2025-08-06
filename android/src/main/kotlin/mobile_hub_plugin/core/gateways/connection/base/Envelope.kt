package br.pucrio.inf.lac.mobilehub.core.gateways.connection.base

import com.google.gson.annotations.SerializedName

data class Envelope<T>(
    @SerializedName("action") val action: String,
    @SerializedName("path") val path: Long? = null,
    @SerializedName("body") val body: T? = null
)