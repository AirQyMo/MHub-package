package br.pucrio.inf.lac.mobilehub.core.di.modules.factories

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

internal interface ChildWorkerFactory {
    fun create(context: Context, params: WorkerParameters): ListenableWorker
}

