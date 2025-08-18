package br.pucrio.inf.lac.mobilehub.core.data.local.mapper

import br.pucrio.inf.lac.mobilehub.core.data.local.models.CepQueryModel
import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery

internal fun CepQuery.toModel() = CepQueryModel(
    id = id,
    name = name,
    statement = statement
)

internal fun CepQueryModel.toEntity() = CepQuery(
    id = id,
    name = name,
    statement = statement
)