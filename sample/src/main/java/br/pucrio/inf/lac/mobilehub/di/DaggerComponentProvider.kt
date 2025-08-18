package br.pucrio.inf.lac.mobilehub.di

internal interface DaggerComponentProvider {
    var component: GraphComponent?
}

internal object SampleInjector : DaggerComponentProvider {
    override var component: GraphComponent? = null
}

internal val injector get() = (SampleInjector as DaggerComponentProvider).component!!
