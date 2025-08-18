package br.pucrio.inf.lac.mobilehub.core.domain.exceptions.technology

class TechnologyNotEnabledException(
    override val message: String = "The technology is not enabled"
) : RuntimeException(message)