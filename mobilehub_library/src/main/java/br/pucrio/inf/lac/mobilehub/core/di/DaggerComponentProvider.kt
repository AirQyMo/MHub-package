package br.pucrio.inf.lac.mobilehub.core.di

import br.pucrio.inf.lac.mobilehub.core.di.components.GraphComponent

internal interface DaggerComponentProvider {
    var component: GraphComponent?
}

internal object MobileHubInjector : DaggerComponentProvider {
    override var component: GraphComponent? = null
}

internal val injector get() = (MobileHubInjector as DaggerComponentProvider).component!!