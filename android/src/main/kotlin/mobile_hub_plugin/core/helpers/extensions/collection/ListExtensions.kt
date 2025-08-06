package br.pucrio.inf.lac.mobilehub.core.helpers.extensions.collection

fun <T> MutableList<T>.reduceToFit(newSize: Int) {
    while (size > newSize) {
        removeAt(size - 1)
    }
}