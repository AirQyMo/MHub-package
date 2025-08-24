package br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep

import br.pucrio.inf.lac.mobilehub.core.domain.entities.Event

interface CEPListener {
    fun onNewEvent(event: Event)
}