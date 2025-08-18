package br.pucrio.inf.lac.ble

import br.pucrio.inf.lac.ble.extensions.stringify

fun Any.load(filename: String) = javaClass.getResourceAsStream(filename)!!.stringify