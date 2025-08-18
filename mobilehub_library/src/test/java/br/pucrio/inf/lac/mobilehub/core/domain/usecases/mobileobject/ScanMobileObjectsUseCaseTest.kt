package br.pucrio.inf.lac.mobilehub.core.domain.usecases.mobileobject

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
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
class ScanMobileObjectsUseCaseTest {
    @Mock
    private lateinit var repository: MobileObjectRepository

    private lateinit var scanMobileObjectsUseCase: ScanMobileObjectsUseCase

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        scanMobileObjectsUseCase = ScanMobileObjectsUseCase(
            repository,
            Schedulers.trampoline(),
            Schedulers.trampoline())
    }

    @Test
    fun scanSuccess() {
        val mobileObject1 = mock(MobileObject::class.java)
        val mobileObject2 = mock(MobileObject::class.java)
        val mobileObject3 = mock(MobileObject::class.java)

        `when`(repository.scan())
            .thenReturn(Flowable.just(mobileObject1, mobileObject2, mobileObject3))

        val observable: Flowable<MobileObject> = scanMobileObjectsUseCase()
        val testObserver = observable.test()
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
            .assertResult(mobileObject1, mobileObject2, mobileObject3)
        testObserver.dispose()
    }

    @Test
    fun scanEmpty() {
        `when`(repository.scan())
            .thenReturn(Flowable.empty())

        val observable = scanMobileObjectsUseCase()
        val testObserver = observable.test()
            .assertSubscribed()
            .assertComplete()
            .assertResult()
        testObserver.dispose()
    }
}