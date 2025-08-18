package br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import java.lang.RuntimeException

@RunWith(JUnit4::class)
class SaveQueryUseCaseTest {
    companion object {
        private const val ID = 1L
    }

    @Mock
    private lateinit var repository: CepQueryRepository

    @Mock
    private lateinit var cep: CEP

    private lateinit var saveQueryUseCase: SaveQueryUseCase

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        saveQueryUseCase = SaveQueryUseCase(
            repository,
            cep,
            Schedulers.trampoline(),
            Schedulers.trampoline())
    }

    @Test
    fun saveSuccess() {
        val entity = mock(CepQuery::class.java)

        `when`(repository.save(entity))
            .thenReturn(Single.just(ID))

        val observable: Single<Long> = saveQueryUseCase(entity)
        val testObserver = observable.test()
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
        testObserver.dispose()
    }

    @Test
    fun saveFailure() {
        val entity = mock(CepQuery::class.java)

        `when`(repository.save(entity))
            .thenReturn(Single.error(RuntimeException()))

        val observable = saveQueryUseCase(entity)
        val testObserver = observable.test()
            .assertSubscribed()
            .assertError(RuntimeException::class.java)

        testObserver.dispose()
    }
}