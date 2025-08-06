package br.pucrio.inf.lac.mobilehub.core.domain.usecases.base

import io.reactivex.Maybe
import io.reactivex.Scheduler

internal abstract class MaybeUseCase<T, in Input> constructor(
    private val backgroundScheduler: Scheduler,
    private val foregroundScheduler: Scheduler
) {
    protected abstract fun generateMaybe(input: Input? = null): Maybe<T>

    operator fun invoke(input: Input? = null): Maybe<T> {
        return generateMaybe(input)
            .subscribeOn(backgroundScheduler)
            .observeOn(foregroundScheduler)
    }
}