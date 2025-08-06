package br.pucrio.inf.lac.mobilehub.core.domain.exceptions.mobileobject

class MobileObjectInvalidConnectionException(
    override val message: String = "The mobile object cannot be connected"
) : RuntimeException(message)