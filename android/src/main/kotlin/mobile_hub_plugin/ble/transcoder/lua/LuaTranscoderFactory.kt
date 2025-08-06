package br.pucrio.inf.lac.ble.transcoder.lua

import br.pucrio.inf.lac.ble.transcoder.Transcoder
import org.luaj.vm2.Globals
import org.luaj.vm2.lib.jse.JsePlatform

class LuaTranscoderFactory private constructor(globals: Globals): Transcoder.Factory {
    companion object {
        fun create() = create(JsePlatform.standardGlobals())

        fun create(globals: Globals) = LuaTranscoderFactory(globals)
    }

    private val transcoder = LuaTranscoder(globals)

    override fun driverTranscoder(config: String?): Transcoder = transcoder
}