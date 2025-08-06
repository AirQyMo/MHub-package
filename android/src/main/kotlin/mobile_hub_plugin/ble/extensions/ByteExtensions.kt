package br.pucrio.inf.lac.ble.extensions

fun ByteArray.shortSignedAtOffset(offset: Int): Int {
    val lowerByte = this[offset].toInt() and 0xFF
    val upperByte = this[offset + 1].toInt()
    return (upperByte shl 8) + lowerByte
}

fun ByteArray.shortUnsignedAtOffset(offset: Int): Int {
    val lowerByte = this[offset].toInt() and 0xFF
    val upperByte = this[offset + 1].toInt() and 0xFF
    return (upperByte shl 8) + lowerByte
}

fun ByteArray.twentyFourBitUnsignedAtOffset(offset: Int): Int {
    val lowerByte = this[offset].toInt() and 0xFF
    val mediumByte = this[offset + 1].toInt() and 0xFF
    val upperByte = this[offset + 2].toInt() and 0xFF
    return (upperByte shl 16) + (mediumByte shl 8) + lowerByte
}