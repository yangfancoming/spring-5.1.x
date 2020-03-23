

package org.springframework.web.reactive.function.client

import org.reactivestreams.Publisher
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Extension for [WebClient.RequestBodySpec.body] providing a `body<Foo>()` variant
 * leveraging Kotlin reified type parameters. This extension is not subject to type
 * erasure and retains actual generic type arguments.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
inline fun <reified T : Any, S : Publisher<T>> RequestBodySpec.body(publisher: S): RequestHeadersSpec<*> =
		body(publisher, object : ParameterizedTypeReference<T>() {})

/**
 * Extension for [WebClient.ResponseSpec.bodyToMono] providing a `bodyToMono<Foo>()` variant
 * leveraging Kotlin reified type parameters. This extension is not subject to type
 * erasure and retains actual generic type arguments.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
inline fun <reified T : Any> WebClient.ResponseSpec.bodyToMono(): Mono<T> =
		bodyToMono(object : ParameterizedTypeReference<T>() {})


/**
 * Extension for [WebClient.ResponseSpec.bodyToFlux] providing a `bodyToFlux<Foo>()` variant
 * leveraging Kotlin reified type parameters. This extension is not subject to type
 * erasure and retains actual generic type arguments.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
inline fun <reified T : Any> WebClient.ResponseSpec.bodyToFlux(): Flux<T> =
		bodyToFlux(object : ParameterizedTypeReference<T>() {})
