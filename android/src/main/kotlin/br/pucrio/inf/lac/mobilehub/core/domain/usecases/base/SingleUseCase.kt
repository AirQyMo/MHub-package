package br.pucrio.inf.lac.mobilehub.core.domain.usecases.base

import io.reactivex.Scheduler
import io.reactivex.Single

internal abstract class SingleUseCase<T, in Input> constructor(
    private val backgroundScheduler: Scheduler,
    private val foregroundScheduler: Scheduler
) {
    protected abstract fun generateSingle(input: Input? = null): Single<T>

    operator fun invoke(input: Input? = null): Single<T> {
        return generateSingle(input)
            .subscribeOn(backgroundScheduler)
            .observeOn(foregroundScheduler)
    }
}