package br.pucrio.inf.lac.mobilehub.core.domain.usecases.event

import br.pucrio.inf.lac.mobilehub.core.domain.entities.Event
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.EventRepository
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
class ListenEventsUseCaseTest {
    @Mock
    private lateinit var repository: EventRepository

    private lateinit var listenEventsUseCase: ListenEventsUseCase

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        listenEventsUseCase = ListenEventsUseCase(
            repository,
            Schedulers.trampoline(),
            Schedulers.trampoline())
    }

    @Test
    fun listenSuccess() {
        val event = mock(Event::class.java)

        `when`(repository.getEvents())
            .thenReturn(Flowable.just(event))

        val observable: Flowable<Event> = listenEventsUseCase()
        val testObserver = observable.test()
            .assertSubscribed()
            .assertNoErrors()
            .assertComplete()
            .assertResult(event)
        testObserver.dispose()
    }
}