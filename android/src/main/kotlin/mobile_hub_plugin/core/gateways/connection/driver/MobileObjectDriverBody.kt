package br.pucrio.inf.lac.mobilehub.core.gateways.connection.driver

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver
import com.google.gson.annotations.SerializedName

internal data class MobileObjectDriverBody(
    @SerializedName("wpan") val wpan: Int,
    @SerializedName("name") val name: String,
    @SerializedName("config") val config: String?,
    @SerializedName("content") val content: String
) {
    fun toEntity() = MobileObjectDriver(
        wpan = wpan,
        name = name,
        config = config,
        content = content
    )
}