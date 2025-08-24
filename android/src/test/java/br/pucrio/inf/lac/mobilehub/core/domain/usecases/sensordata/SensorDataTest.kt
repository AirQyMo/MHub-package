package br.pucrio.inf.lac.mobilehub.core.domain.usecases.sensordata

import br.pucrio.inf.lac.mobilehub.core.domain.entities.Moid
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SensorDataTest {
    private val gson = Gson()

    @Test
    fun parseSuccess() {
        val sensorData = SensorData(
            serviceName = "temperature",
            serviceData = listOf(21.2)
        ).apply {
            mobileObjectId = Moid(1, "test")
        }

        val jsonObject = gson.toJson(sensorData)
        val entity = gson.fromJson(jsonObject, SensorData::class.java)
        assertEquals(sensorData, entity)
        assertEquals(sensorData.mobileObjectId, entity.mobileObjectId)
    }
}