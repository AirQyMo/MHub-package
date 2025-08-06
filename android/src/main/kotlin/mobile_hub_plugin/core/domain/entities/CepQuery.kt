package br.pucrio.inf.lac.mobilehub.core.domain.entities

data class CepQuery(
    val id: Long? = null,
    var name: String,
    var statement: String
)