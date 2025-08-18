package br.pucrio.inf.lac.mobilehub.core.helpers.extensions.traffic

const val B: Long = 1
const val KB = B * 1024
const val MB = KB * 1024
const val GB = MB * 1024

fun Double.toSpeed(inBits: Boolean = false): Double = if (inBits) this * 8 else this

fun Double.parseSpeed(inBits: Boolean): String {
    val value = if (inBits) this * 8 else this
    return when {
        value < KB -> String.format("%.1f " + (if (inBits) "b" else "B") + "/s", value)
        value < MB -> String.format("%.1f K" + (if (inBits) "b" else "B") + "/s", value / KB)
        value < GB -> String.format("%.1f M" + (if (inBits) "b" else "B") + "/s", value / MB)
        else -> String.format("%.2f G" + (if (inBits) "b" else "B") + "/s", value / GB)
    }
}
