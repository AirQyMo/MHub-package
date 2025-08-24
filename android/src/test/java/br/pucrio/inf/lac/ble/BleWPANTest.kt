package br.pucrio.inf.lac.ble

import android.content.Context
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.driver.MobileObjectDriverException
import com.polidea.rxandroidble2.RxBleClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class BleWPANTest {
    companion object {
        private const val ID = 1

        private const val SENSOR_TAG_NAME = "CC2650SensorTag"
        private const val SENSOR_TAG_MAC_ADDRESS = "00:11:22:33:44:55"
    }

    @Mock
    private lateinit var context: Context

    private lateinit var bleClient: RxBleClient

    private lateinit var wpan: BleWPAN

    private val bleClientBuilder = BleClientBuilder().apply {
        addDevice(SENSOR_TAG_MAC_ADDRESS, SENSOR_TAG_NAME)
    }

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        bleClient = bleClientBuilder.build()
        wpan = BleWPAN.Builder(context)
            .setClient(bleClient)
            .build()
    }

    @Test
    fun getIdSuccessful() {
        assertEquals(wpan.id, ID)
    }

    @Test
    fun startScanSuccessful() {
        wpan.startScan()
            .test()
            .assertSubscribed()
            .assertNoErrors()
            .dispose()
    }

    @Test
    fun connectNoModuleFailure() {
        val mobileObject = mock(MobileObject::class.java)

        `when`(mobileObject.name)
            .thenReturn("OtherDevice")

        wpan.connect(mobileObject)
            .test()
            .assertSubscribed()
            .assertError(MobileObjectDriverException::class.java)
            .dispose()
    }
}
