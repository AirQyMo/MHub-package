package br.pucrio.inf.lac.mobilehub.core.domain.entities

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic

data class Message(
    val topic: Topic,
    val payload: String
)