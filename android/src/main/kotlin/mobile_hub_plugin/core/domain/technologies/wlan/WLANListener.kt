package br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan

import br.pucrio.inf.lac.mobilehub.core.domain.entities.Message

interface WLANListener {
    fun onConnected()

    fun onDisconnected()

    fun onNewMessage(message: Message)
}