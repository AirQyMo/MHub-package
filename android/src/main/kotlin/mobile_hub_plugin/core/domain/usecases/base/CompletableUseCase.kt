package br.pucrio.inf.lac.mobilehub.core.domain.usecases.base

import io.reactivex.Completable
import io.reactivex.Scheduler

internal abstract class CompletableUseCase<in Input> constructor(
    private val backgroundScheduler: Scheduler,
    private val foregroundScheduler: Scheduler
) {
    protected abstract fun generateCompletable(input: Input? = null): Completable

    operator fun invoke(input: Input? = null): Completable {
        return generateCompletable(input)
            .subscribeOn(backgroundScheduler)
            .observeOn(foregroundScheduler)
    }
}