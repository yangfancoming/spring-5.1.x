

package org.springframework.web.reactive.function.client

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.ResponseEntity
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Extension for [ClientResponse.bodyToMono] providing a `bodyToMono<Foo>()` variant
 * leveraging Kotlin reified type parameters. This extension is not subject to type
 * erasure and retains actual generic type arguments.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
inline fun <reified T : Any> ClientResponse.bodyToMono(): Mono<T> =
		bodyToMono(object : ParameterizedTypeReference<T>() {})

/**
 * Extension for [ClientResponse.bodyToFlux] providing a `bodyToFlux<Foo>()` variant
 * leveraging Kotlin reified type parameters. This extension is not subject to type
 * erasure and retains actual generic type arguments.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
inline fun <reified T : Any> ClientResponse.bodyToFlux(): Flux<T> =
		bodyToFlux(object : ParameterizedTypeReference<T>() {})

/**
 * Extension for [ClientResponse.toEntity] providing a `toEntity<Foo>()` variant
 * leveraging Kotlin reified type parameters. This extension is not subject to type
 * erasure and retains actual generic type arguments.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
inline fun <reified T : Any> ClientResponse.toEntity(): Mono<ResponseEntity<T>> =
		toEntity(object : ParameterizedTypeReference<T>() {})

/**
 * Extension for [ClientResponse.toEntityList] providing a `bodyToEntityList<Foo>()` variant
 * leveraging Kotlin reified type parameters. This extension is not subject to type
 * erasure and retains actual generic type arguments.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
inline fun <reified T : Any> ClientResponse.toEntityList(): Mono<ResponseEntity<List<T>>> =
		toEntityList(object : ParameterizedTypeReference<T>() {})
