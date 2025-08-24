package br.pucrio.inf.lac.mobilehub.core.domain.exceptions.technology

class TechnologyNotSupportedException(
    override val message: String = "The technology is not supported"
) : RuntimeException(message)