package br.pucrio.inf.lac.mobilehub.core.helpers.components.bus

import io.reactivex.Observable

internal interface Bus<T> {
    fun publish(event: T)

    fun <E : T> on(eventType: Class<E>): Observable<E>
}
