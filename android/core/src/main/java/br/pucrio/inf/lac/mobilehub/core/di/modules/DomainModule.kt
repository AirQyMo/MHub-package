package br.pucrio.inf.lac.mobilehub.core.di.modules

import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Background
import br.pucrio.inf.lac.mobilehub.core.domain.qualifiers.Foreground
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton

@Module
internal class DomainModule {
    @Singleton
    @Provides
    @Background
    fun providesBackgroundScheduler(): Scheduler = Schedulers.io()

    @Singleton
    @Provides
    @Foreground
    fun providesForegroundScheduler(): Scheduler = AndroidSchedulers.mainThread()
}