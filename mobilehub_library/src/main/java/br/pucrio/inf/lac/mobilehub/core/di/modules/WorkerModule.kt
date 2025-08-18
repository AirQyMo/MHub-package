package br.pucrio.inf.lac.mobilehub.core.di.modules

import br.pucrio.inf.lac.mobilehub.core.di.modules.factories.ChildWorkerFactory
import br.pucrio.inf.lac.mobilehub.core.di.modules.factories.WorkerKey
import br.pucrio.inf.lac.mobilehub.core.data.remote.BufferTransmissionWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(BufferTransmissionWorker::class)
    fun bindBufferTransmissionWorker(factory: BufferTransmissionWorker.Factory): ChildWorkerFactory
}