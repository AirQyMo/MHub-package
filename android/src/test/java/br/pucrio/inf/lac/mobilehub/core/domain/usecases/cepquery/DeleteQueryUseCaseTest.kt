package br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.exceptions.general.NotFoundException
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class DeleteQueryUseCaseTest {
    companion object {
        private const val ID = 1L
    }

    @Mock
    private lateinit var repository: CepQueryRepository

    private lateinit var deleteQueryUseCase: DeleteQueryUseCase

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        deleteQueryUseCase = DeleteQueryUseCase(
            repository,
            Schedulers.trampoline(),
            Schedulers.trampoline())
    }

    @Test
    fun deleteSuccess() {
        val entity = mock(CepQuery::class.java)

        `when`(repository.findById(ID))
            .thenReturn(Maybe.just(entity))

        `when`(repository.delete(ID))
            .thenReturn(Completable.complete())

        val observable: Completable = deleteQueryUseCase(ID)
        val testObserver = observable.test()
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
        testObserver.dispose()
    }

    @Test
    fun deleteFailure() {
        `when`(repository.findById(ID))
            .thenReturn(Maybe.empty())

        val observable = deleteQueryUseCase(ID)
        val testObserver = observable.test()
            .assertSubscribed()
            .assertError(NotFoundException::class.java)

        testObserver.dispose()
    }
}