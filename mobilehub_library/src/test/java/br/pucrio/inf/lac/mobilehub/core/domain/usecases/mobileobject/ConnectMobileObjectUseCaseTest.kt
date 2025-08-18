package br.pucrio.inf.lac.mobilehub.core.domain.usecases.mobileobject

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.entities.SensorData
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectRepository
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class ConnectMobileObjectUseCaseTest {
    @Mock
    private lateinit var repository: MobileObjectRepository

    private lateinit var connectMobileObjectUseCase: ConnectMobileObjectUseCase

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        connectMobileObjectUseCase = ConnectMobileObjectUseCase(
            repository,
            Schedulers.trampoline(),
            Schedulers.trampoline())
    }

    @Test
    fun connectSuccess() {
        val mobileObject = mock(MobileObject::class.java)
        val sensorData = mock(SensorData::class.java)

        `when`(repository.connect(mobileObject))
            .thenReturn(Flowable.just(sensorData))

        val observable: Flowable<SensorData> = connectMobileObjectUseCase(mobileObject)
        val testObserver = observable.test()
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
            .assertResult(sensorData)
        testObserver.dispose()
    }

    @Test
    fun connectFailure() {
        val mobileObject = mock(MobileObject::class.java)

        `when`(repository.connect(mobileObject))
            .thenReturn(Flowable.empty())

        val observable = connectMobileObjectUseCase(mobileObject)
        val testObserver = observable.test()
            .assertSubscribed()
            .assertComplete()
            .assertResult()
        testObserver.dispose()
    }
}