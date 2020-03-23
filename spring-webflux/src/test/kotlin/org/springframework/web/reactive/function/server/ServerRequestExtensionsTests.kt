

package org.springframework.web.reactive.function.client

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.bodyToFlux
import org.springframework.web.reactive.function.server.bodyToMono

/**
 * Mock object based tests for [ServerRequest] Kotlin extensions.
 *
 * @author Sebastien Deleuze
 */
@RunWith(MockitoJUnitRunner::class)
class ServerRequestExtensionsTests {

	@Mock(answer = Answers.RETURNS_MOCKS)
	lateinit var request: ServerRequest

	@Test
	fun `bodyToMono with reified type parameters`() {
		request.bodyToMono<List<Foo>>()
		verify(request, times(1)).bodyToMono(object : ParameterizedTypeReference<List<Foo>>() {})
	}

	@Test
	fun `bodyToFlux with reified type parameters`() {
		request.bodyToFlux<List<Foo>>()
		verify(request, times(1)).bodyToFlux(object : ParameterizedTypeReference<List<Foo>>() {})
	}

	class Foo
}
