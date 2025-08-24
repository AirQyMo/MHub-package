package br.pucrio.inf.lac.mobilehub.core.data.repositories

import br.pucrio.inf.lac.mobilehub.core.data.buffer.BufferStrategy
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wpan.WPAN
import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectRepository
import io.reactivex.Flowable
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class MobileObjectRepositoryTest {
    companion object {
        private const val WPAN1_ID = 1
        private const val WPAN2_ID = 2
    }

    private val wpanTechnologies = hashMapOf<Int, WPAN>()

    @Mock
    private lateinit var bufferStrategy: BufferStrategy

    @Mock
    private lateinit var wlanTechnology: WLAN

    @Mock
    private lateinit var wpanTechnology1: WPAN

    @Mock
    private lateinit var wpanTechnology2: WPAN

    @Mock
    private lateinit var cepTechnology: CEP

    private lateinit var repository: MobileObjectRepository

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        wpanTechnologies[WPAN1_ID] = wpanTechnology1
        wpanTechnologies[WPAN2_ID] = wpanTechnology2
        repository = MobileObjectRepositoryImpl(bufferStrategy, wlanTechnology, wpanTechnologies, cepTechnology)
    }

    @Test
    fun scanSuccess() {
        val mobileObject1 = mock(MobileObject::class.java)
        val mobileObject2 = mock(MobileObject::class.java)
        val mobileObject3 = mock(MobileObject::class.java)

        `when`(wpanTechnology1.startScan())
            .thenReturn(Flowable.just(mobileObject1, mobileObject2))

        `when`(wpanTechnology2.startScan())
            .thenReturn(Flowable.just(mobileObject3))

        val observable: Flowable<MobileObject> = repository.scan()
        val testObserver = observable.test()
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
            .assertResult(mobileObject1, mobileObject2, mobileObject3)
        testObserver.dispose()
    }

    @Test
    fun scanEmpty() {
        val mobileObject1 = mock(MobileObject::class.java)
        val mobileObject2 = mock(MobileObject::class.java)

        `when`(wpanTechnology1.startScan())
            .thenReturn(Flowable.just(mobileObject1, mobileObject2))

        `when`(wpanTechnology2.startScan())
            .thenReturn(Flowable.empty())

        val observable = repository.scan()
        val testObserver = observable.test()
            .assertSubscribed()
            .assertComplete()
            .assertResult(mobileObject1, mobileObject2)
        testObserver.dispose()
    }
}