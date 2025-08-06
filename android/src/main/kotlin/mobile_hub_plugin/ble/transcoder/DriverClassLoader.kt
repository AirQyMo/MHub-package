package br.pucrio.inf.lac.ble.transcoder

internal class DriverClassLoader : ClassLoader() {
    private object List {
        val WHITE = arrayOf(
            "br\\.pucrio\\.inf\\.lac\\.ble\\.device\\..*"
        )

        val BLACK = arrayOf(
            "java\\.lang\\.Class",
            "java\\.lang\\.ClassLoader",
            "java\\.lang\\.reflect\\..*",
            "java\\.io\\.File"
        )

        val REPLACE = arrayOf<Array<String>>()
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String): Class<*> {
        checkBlackList(name)

        var clazz = checkWhiteList(name)
        if (clazz != null) {
            return clazz
        }

        clazz = checkReplaceList(name)
        if (clazz != null) {
            return clazz
        }

        return getSystemClassLoader().loadClass(name)
    }

    private fun checkBlackList(name: String) {
        for (pattern in List.BLACK) {
            if (name.matches(pattern.toRegex())) {
                throw ClassNotFoundException(name)
            }
        }
    }

    private fun checkWhiteList(name: String): Class<*>? {
        for (pattern in List.WHITE) {
            if (name.matches(pattern.toRegex())) {
                return Class.forName(name)
            }
        }

        return null
    }

    private fun checkReplaceList(name: String): Class<*>? {
        for (pair in List.REPLACE) {
            if (name.matches(pair[0].toRegex())) {
                return Class.forName(pair[1])
            }
        }

        return null
    }
}
