package br.pucrio.inf.lac.mqtt

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.gateways.connection.base.Envelope
import com.google.gson.Gson
import info.mqtt.android.service.MqttAndroidClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class MqttWLANTest {
    @Mock
    private lateinit var mqttClient: MqttAndroidClient

    private val gson: Gson = Gson()

    private lateinit var wlan: MqttWLAN

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        gson
        wlan = MqttWLAN(mqttClient, gson)
    }

    @Test
    fun publishRequestSuccessful() {
        val body = mapOf<String, Any>(
            "wpan" to 1,
            "name" to "Test"
        )

        val request = Envelope(
            action = "getDriver",
            body = body
        )

        `when`(mqttClient.clientId)
            .thenReturn("paho1616357950879000000")

        /*wlan.publishRequest(Topic.Driver, request)
            .test()
            .assertSubscribed()
            .assertNoErrors()
            .dispose()*/
    }
}
