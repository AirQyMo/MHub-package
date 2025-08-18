package br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class GetQueryUseCaseTest {
    companion object {
        private const val ID = 1L
    }

    @Mock
    private lateinit var repository: CepQueryRepository

    private lateinit var getQueryUseCase: GetQueryUseCase

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        getQueryUseCase = GetQueryUseCase(
            repository,
            Schedulers.trampoline(),
            Schedulers.trampoline())
    }

    @Test
    fun getSuccess() {
        val entity = mock(CepQuery::class.java)

        `when`(repository.findById(ID))
            .thenReturn(Maybe.just(entity))

        val observable = getQueryUseCase(ID)
        val testObserver = observable.test()
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
            .assertValue(entity)

        testObserver.dispose()
    }

    @Test
    fun getEmptySuccess() {
        `when`(repository.findById(ID))
            .thenReturn(Maybe.empty())

        val observable = getQueryUseCase(ID)
        val testObserver = observable.test()
            .assertSubscribed()
            .assertComplete()

        testObserver.dispose()
    }
}