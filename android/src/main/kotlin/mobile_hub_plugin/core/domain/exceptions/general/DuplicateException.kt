package br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general

class DuplicateException(override val message: String = "Already exists") : RuntimeException(message)