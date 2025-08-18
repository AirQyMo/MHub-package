package br.pucrio.inf.lac.mobilehub.core.domain.usecases.message

import br.pucrio.inf.lac.mobilehub.core.domain.entities.base.Either
import br.pucrio.inf.lac.mobilehub.core.domain.entities.Message
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MessageRepository
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
class ConnectUseCaseTest {
    @Mock
    private lateinit var repository: MessageRepository

    private lateinit var connectUseCase: ConnectUseCase

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        connectUseCase = ConnectUseCase(
            repository,
            Schedulers.trampoline(),
            Schedulers.trampoline())
    }

    @Test
    fun connectSuccess() {
        val message = mock(Message::class.java)

        `when`(repository.connect())
            .thenReturn(Flowable.just(Either.Left(message)))

        val observable: Flowable<Message> = connectUseCase()
        val testObserver = observable.test()
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
            .assertResult(message)
        testObserver.dispose()
    }

    @Test
    fun connectFailure() {
        `when`(repository.connect())
            .thenReturn(Flowable.empty())

        val observable = connectUseCase()
        val testObserver = observable.test()
            .assertSubscribed()
            .assertComplete()
            .assertResult()
        testObserver.dispose()
    }
}