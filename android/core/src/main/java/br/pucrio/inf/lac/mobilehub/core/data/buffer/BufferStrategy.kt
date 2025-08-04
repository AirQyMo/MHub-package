package br.pucrio.inf.lac.mobilehub.core.data.buffer

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN

interface BufferStrategy {
    fun handleBuffer(wlanTechnology: WLAN)
}

