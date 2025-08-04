package br.pucrio.inf.lac.mobilehub.core.domain.usecases.mobileobject

import br.pucrio.inf.lac.mobilehub.core.domain.entities.MobileObject
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import br.pucrio.inf.lac.mobilehub.core.domain.repositories.MobileObjectRepository
import br.pucrio.inf.lac.mobilehub.core.domain.usecases.base.FlowableUseCase
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.kotlin.isNotNull
import io.reactivex.Flowable
import io.reactivex.Scheduler
import javax.inject.Inject

internal class ScanMobileObjectsUseCase @Inject constructor(
    private val repository: MobileObjectRepository,
    @Background backgroundScheduler: Scheduler,
    @Foreground foregroundScheduler: Scheduler
) : FlowableUseCase<MobileObject, Void>(backgroundScheduler, foregroundScheduler) {
    override fun generateFlowable(input: Void?): Flowable<MobileObject> = repository.scan()
}