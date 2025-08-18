package br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general.NotFoundException
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.cep.CEP
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class UpdateQueryUseCaseTest {
    companion object {
        private const val ID = 1L
    }

    @Mock
    private lateinit var repository: CepQueryRepository

    @Mock
    private lateinit var cep: CEP

    private lateinit var updateQueryUseCase: UpdateQueryUseCase

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        updateQueryUseCase = UpdateQueryUseCase(
            repository,
            cep,
            Schedulers.trampoline(),
            Schedulers.trampoline())
    }

    @Test
    fun updateSuccess() {
        val entity = mock(CepQuery::class.java)

        `when`(repository.findById(ID))
            .thenReturn(Maybe.just(entity))

        `when`(repository.save(entity))
            .thenReturn(Single.just(ID))

        val observable = updateQueryUseCase(UpdateQueryUseCase.Params(ID, entity))
        val testObserver = observable.test()
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
            .assertValue(ID)

        testObserver.dispose()
    }

    @Test
    fun updateNotFoundFailure() {
        val entity = mock(CepQuery::class.java)

        `when`(repository.findById(ID))
            .thenReturn(Maybe.empty())

        val observable = updateQueryUseCase(UpdateQueryUseCase.Params(ID, entity))
        val testObserver = observable.test()
            .assertSubscribed()
            .assertError(NotFoundException::class.java)

        testObserver.dispose()
    }

    @Test
    fun updateFailure() {
        val entity = mock(CepQuery::class.java)

        `when`(repository.findById(ID))
            .thenReturn(Maybe.just(entity))

        `when`(repository.save(entity))
            .thenReturn(Single.error(RuntimeException()))

        val observable = updateQueryUseCase(UpdateQueryUseCase.Params(ID, entity))
        val testObserver = observable.test()
            .assertSubscribed()
            .assertError(RuntimeException::class.java)

        testObserver.dispose()
    }
}