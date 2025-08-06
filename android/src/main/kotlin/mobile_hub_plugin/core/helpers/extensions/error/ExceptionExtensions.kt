package br.pucrio.inf.lac.mobilehub.core.helpers.extensions.error

import java.lang.Exception

fun ignoreException(action: () -> Unit) =
    try {
        action()
    } catch (ex: Exception) {
        ex.printStackTrace()
    }