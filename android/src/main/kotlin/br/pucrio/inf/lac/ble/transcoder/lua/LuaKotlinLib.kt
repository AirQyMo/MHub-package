package br.pucrio.inf.lac.ble.transcoder.lua

import br.pucrio.inf.lac.ble.transcoder.DriverClassLoader
import org.luaj.vm2.lib.jse.LuajavaLib

internal class LuaKotlinLib : LuajavaLib() {
    private val classLoader = DriverClassLoader()

    override fun classForName(name: String): Class<*> = classLoader.loadClass(name)
}