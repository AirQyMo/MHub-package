package br.pucrio.inf.lac.mobilehub.core.domain.usecases.cepquery

import br.pucrio.inf.lac.mobilehub.core.domain.entities.CepQuery
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.CepQueryRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class GetQueriesUseCaseTest {
    @Mock
    private lateinit var repository: CepQueryRepository

    private lateinit var getQueriesUseCase: GetQueriesUseCase

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        getQueriesUseCase = GetQueriesUseCase(
            repository,
            Schedulers.trampoline(),
            Schedulers.trampoline())
    }

    @Test
    fun listSuccess() {
        val entity1 = mock(CepQuery::class.java)
        val entity2 = mock(CepQuery::class.java)
        val entities = listOf<CepQuery>(entity1, entity2)

        `when`(repository.list())
            .thenReturn(Single.just(entities))

        val observable: Single<List<CepQuery>> = getQueriesUseCase()
        val testObserver = observable.test()
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
            .assertValue(entities)
        testObserver.dispose()
    }
}