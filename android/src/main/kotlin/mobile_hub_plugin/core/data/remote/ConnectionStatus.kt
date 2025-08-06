package br.pucrio.inf.lac.mobilehub.core.data.remote

sealed class ConnectionStatus {
    object Connecting: ConnectionStatus()
    object Connected: ConnectionStatus()
    object Disconnected: ConnectionStatus()

    val isConnected: Boolean
        get() = this == Connected

    val isConnectingOrConnected
        get() = this == Connected || this == Connecting
}