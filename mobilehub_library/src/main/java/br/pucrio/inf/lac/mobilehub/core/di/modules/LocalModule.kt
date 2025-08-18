package br.pucrio.inf.lac.mobilehub.core.di.modules

import android.content.Context
import br.pucrio.inf.lac.mobilehub.core.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class LocalModule {
    @Provides
    @Singleton
    fun providesDatabase(context: Context) = AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun providesCepQueryDao(database: AppDatabase) = database.cepQueryDao()

    @Provides
    @Singleton
    fun providesMobileObjectDriverDao(database: AppDatabase) = database.mobileObjectDriverDao()
}