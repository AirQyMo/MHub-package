package br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general

class NotFoundException(override val message: String = "Not found") : RuntimeException(message)