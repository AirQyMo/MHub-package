package br.pucrio.inf.lac.ble.extensions

import org.luaj.vm2.LuaString
import org.luaj.vm2.LuaTable
import org.luaj.vm2.LuaValue
import java.io.InputStream
import java.nio.charset.StandardCharsets

val String.asLua: LuaString
    get() = LuaValue.valueOf(this)

val ByteArray.asLua: LuaString
    get() = LuaValue.valueOf(this)

val LuaTable.asDoubleList: List<Double>
    get() {
        val collection = mutableListOf<Double>()
        for (key in this.keys()) {
            collection.add(this[key].todouble())
        }
        return collection
    }

val InputStream.stringify
    get() = String(readBytes(), StandardCharsets.UTF_8)

inline fun <reified T> LuaTable.toList(
    additional: (Int, T) -> T = { _, sensor -> sensor }
): List<T> {
    val list = mutableListOf<T>()

    for (key in keys()) {
        var element = this[key].checkuserdata(T::class.java) as T
        element = additional(key.toint(), element)
        list.add(element)
    }

    return list
}
