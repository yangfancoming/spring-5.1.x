

package org.springframework.web.reactive.function.server

import org.springframework.core.ParameterizedTypeReference
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Extension for [ServerRequest.bodyToMono] providing a `bodyToMono<Foo>()` variant
 * leveraging Kotlin reified type parameters. This extension is not subject to type
 * erasure and retains actual generic type arguments.
 * 
 * @author Sebastien Deleuze
 * @since 5.0
 */
inline fun <reified T : Any> ServerRequest.bodyToMono(): Mono<T> =
		bodyToMono(object : ParameterizedTypeReference<T>() {})

/**
 * Extension for [ServerRequest.bodyToFlux] providing a `bodyToFlux<Foo>()` variant
 * leveraging Kotlin reified type parameters. This extension is not subject to type
 * erasure and retains actual generic type arguments.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
inline fun <reified T : Any> ServerRequest.bodyToFlux(): Flux<T> =
		bodyToFlux(object : ParameterizedTypeReference<T>() {})
