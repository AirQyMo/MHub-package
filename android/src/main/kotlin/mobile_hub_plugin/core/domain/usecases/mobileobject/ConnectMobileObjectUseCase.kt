package br.pucrio.inf.lac.mobilehub.core.domain.usecases.mobileobject

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.CompletableUseCase
import io.reactivex.Completable
import io.reactivex.Scheduler
import javax.inject.Inject

internal class ConnectMobileObjectUseCase @Inject constructor(
    private val repository: MobileObjectRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : CompletableUseCase<MobileObject>(backgroundScheduler, foregroundScheduler) {
    override fun generateCompletable(input: MobileObject?): Completable {
        requireNotNull(input) { "Mobile object can't be null" }
        return repository.connect(input)
    }
}