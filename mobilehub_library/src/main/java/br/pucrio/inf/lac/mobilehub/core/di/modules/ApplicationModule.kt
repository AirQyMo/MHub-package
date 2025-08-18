package br.pucrio.inf.lac.mobilehub.core.di.modules

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@Module(includes = [AssistedInject_ApplicationModule::class])
@AssistedModule
internal abstract class ApplicationModule
