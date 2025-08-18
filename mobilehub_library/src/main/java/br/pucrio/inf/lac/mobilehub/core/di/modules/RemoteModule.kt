package br.pucrio.inf.lac.mobilehub.core.di.modules

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class RemoteModule {
    @Provides
    @Singleton
    fun providesGson() = Gson()
}
