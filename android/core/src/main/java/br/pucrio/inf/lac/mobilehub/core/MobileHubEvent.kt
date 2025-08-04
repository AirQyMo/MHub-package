package br.pucrio.inf.lac.mobilehub.core

import br.pucrio.inf.lac.mobilehub.core.domain.entities.Event
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Message
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData

sealed class MobileHubEvent {
    data class Status(val isStarted: Boolean) : MobileHubEvent()
    data class MobileObjectDiscovered(val mobileObject: MobileObject) : MobileHubEvent()
    data class MobileObjectConnected(val mobileObject: MobileObject) : MobileHubEvent()
    data class NewContextData(val contextData: List<SensorData>) : MobileHubEvent()
    data class NewSensorData(val sensorData: SensorData) : MobileHubEvent()
    data class NewEvent(val event: Event) : MobileHubEvent()
    data class NewMessage(val message: Message) : MobileHubEvent()
}
