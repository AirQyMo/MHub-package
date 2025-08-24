package br.pucrio.inf.lac.mobilehub.core.domain.exceptions.driver

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject

class MobileObjectDriverException(
    val mobileObject: MobileObject,
    override val message: String = "There is no available driver for the device"
) : RuntimeException(message)