

package org.springframework.web.reactive.function.client

import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.reactivestreams.Publisher
import org.springframework.core.ParameterizedTypeReference

/**
 * Mock object based tests for [WebClient] Kotlin extensions
 *
 * @author Sebastien Deleuze
 */
@RunWith(MockitoJUnitRunner::class)
class WebClientExtensionsTests {

	@Mock(answer = Answers.RETURNS_MOCKS)
	lateinit var requestBodySpec: WebClient.RequestBodySpec

	@Mock(answer = Answers.RETURNS_MOCKS)
	lateinit var responseSpec: WebClient.ResponseSpec


	@Test
	fun `RequestBodySpec#body with Publisher and reified type parameters`() {
		val body = mock<Publisher<List<Foo>>>()
		requestBodySpec.body(body)
		verify(requestBodySpec, times(1)).body(body, object : ParameterizedTypeReference<List<Foo>>() {})
	}

	@Test
	fun `ResponseSpec#bodyToMono with reified type parameters`() {
		responseSpec.bodyToMono<List<Foo>>()
		verify(responseSpec, times(1)).bodyToMono(object : ParameterizedTypeReference<List<Foo>>() {})
	}

	@Test
	fun `ResponseSpec#bodyToFlux with reified type parameters`() {
		responseSpec.bodyToFlux<List<Foo>>()
		verify(responseSpec, times(1)).bodyToFlux(object : ParameterizedTypeReference<List<Foo>>() {})
	}

	class Foo
}
