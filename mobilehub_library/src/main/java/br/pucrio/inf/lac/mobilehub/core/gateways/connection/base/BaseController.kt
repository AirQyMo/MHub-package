package br.pucrio.inf.lac.mobilehub.core.gateways.connection.base

import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.QoS
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.Topic
import br.pucrio.inf.lac.mobilehub.core.domain.technologies.wlan.WLAN
import br.pucrio.inf.lac.mobilehub.core.helpers.extensions.collection.reduceToFit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber
import java.lang.reflect.Method

internal abstract class BaseController(
    private val gson: Gson,
    private val wlanTechnology: WLAN,
    private val topic: Topic
) {
    companion object {
        private const val SUCCESS = "Success"
    }

    private val methods = getMethodsAnnotatedWithRequestMapping(this::class.java)

    private fun getMethodsAnnotatedWithRequestMapping(type: Class<*>): Map<String, Method> {
        val annotation = RequestMapping::class.java
        val methods = mutableMapOf<String, Method>()

        var klass = type
        while (klass != Any::class.java) {
            for (method in klass.declaredMethods) {
                if (method.isAnnotationPresent(annotation)) {
                    val instance = method.getAnnotation(annotation)
                    methods[instance!!.action] = method
                }
            }
            klass = klass.superclass!!
        }
        return methods
    }

    inline fun <reified T> route(payload: String) {
        val request = gson.fromJsonEnvelope(payload, T::class.java)
        mapToMethod(request)
    }

    private inline fun <reified T> Gson.fromJsonEnvelope(json: String, clazz: Class<T>): Envelope<T> {
        val type = TypeToken.getParameterized(Envelope::class.java, clazz).type
        return fromJson(json, type)
    }

    private fun <T> mapToMethod(request: Envelope<T>) {
        val method = methods[request.action]
        val parametersCount = method?.parameterTypes?.size ?: 0
        val parameters = mutableListOf<Any?>()

        request.path?.let {
            parameters.add(it)
        }

        request.body?.let {
            parameters.add(it)
        }

        parameters.reduceToFit(parametersCount)
        val result = method?.invoke(this, *parameters.toTypedArray())
        handleResult(result)
    }

    private fun handleResult(result: Any?) {
        when (result) {
            is Single<*> -> result.andPublish(topic)
            is Maybe<*> -> result.andPublish(topic)
            is Completable -> result.andPublish(topic)
            null -> Timber.e("Result is null")
            else -> Timber.e("Result not supported: ${result::class.java.simpleName}")
        }
    }

    private fun <M : Any> Single<M>.andPublish(topic: Topic) = flatMapCompletable {
        publishJson(topic, it)
    }.subscribe(::onSuccess) { onError(topic, it) }

    private fun <M : Any> Maybe<M>.andPublish(topic: Topic) = flatMapCompletable {
        publishJson(topic, it)
    }.subscribe(::onSuccess) { onError(topic, it) }

    private fun Completable.andPublish(topic: Topic) = andThen {
        publishText(topic, SUCCESS)
    }.subscribe(::onSuccess) { onError(topic, it) }

    private fun publishText(topic: Topic, text: String) = wlanTechnology.publishMessage(topic, text, QoS.ExactlyOnce)

    private fun <M> publishJson(topic: Topic, message: M): Completable {
        val payload = gson.toJson(message)
        return wlanTechnology.publishResponse(topic, payload)
    }

    private fun onSuccess() = Timber.i("Completed")

    private fun onError(topic: Topic, error: Throwable) {
        publishText(topic, error.localizedMessage!!).subscribe()
    }
}