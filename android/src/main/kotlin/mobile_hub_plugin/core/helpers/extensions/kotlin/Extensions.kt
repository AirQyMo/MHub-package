package br.pucrio.inf.lac.mobilehub.core.helpers.extensions.kotlin

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun Any?.isNull(): Boolean = this == null

@ExperimentalContracts
fun Any?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }

    return this != null
}