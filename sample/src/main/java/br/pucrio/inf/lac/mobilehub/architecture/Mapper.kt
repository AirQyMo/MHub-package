package br.pucrio.inf.lac.mobilehub.architecture

interface Mapper<I, O> {

    fun from(input: I): O
}
