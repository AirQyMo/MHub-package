package br.pucrio.inf.lac.mobilehub.core.gateways.connection.base

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
internal annotation class RequestMapping(val action : String)