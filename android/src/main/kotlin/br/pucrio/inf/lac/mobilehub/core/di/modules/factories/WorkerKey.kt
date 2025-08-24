package br.pucrio.inf.lac.mobilehub.core.di.modules.factories

import androidx.work.ListenableWorker
import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
internal annotation class WorkerKey(val value: KClass<out ListenableWorker>)