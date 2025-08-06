package br.pucrio.inf.lac.mobilehub.core.gateways.connection.cep

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import com.google.gson.annotations.SerializedName

internal data class CepQueryBody(
    @SerializedName("name") val name: String,
    @SerializedName("statement") val statement: String
) {
    fun toEntity() = CepQuery(
        name = name,
        statement = statement
    )
}