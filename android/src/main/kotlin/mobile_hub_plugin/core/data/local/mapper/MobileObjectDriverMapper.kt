package br.pucrio.inf.lac.mobilehub.core.data.local.mapper

import br.pucrio.inf.lac.mobilehub.core.data.local.models.MobileObjectDriverModel
import br.pucrio.inf.lac.mobilehub.core.data.remote.models.MobileObjectDriverDto
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObjectDriver

internal fun MobileObjectDriver.toModel() = MobileObjectDriverModel(
    id = id,
    wpan = wpan,
    name = name,
    config = config,
    content = content
)

internal fun MobileObjectDriverModel.toEntity() = MobileObjectDriver(
    id = id,
    wpan = wpan,
    name = name,
    config = config,
    content = content
)

internal fun MobileObjectDriverDto.toModel() = MobileObjectDriverModel(
    id = id,
    wpan = wpan,
    name = name,
    config = config,
    content = content,
)
