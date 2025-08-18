package br.pucrio.inf.lac.mobilehub.core.domain.exceptions.technology

class TechnologyDisconnectedException(
    override val message: String = "The technology is not connected"
) : RuntimeException(message)
