package br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general

class PermissionException(override val message: String = "There is a missing permission") : RuntimeException(message)