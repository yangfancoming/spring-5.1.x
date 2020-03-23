

package org.springframework.web.reactive.function.client

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.core.ParameterizedTypeReference

/**
 * Mock object based tests for [ClientResponse] Kotlin extensions.
 *
 * @author Sebastien Deleuze
 */
@RunWith(MockitoJUnitRunner::class)
class ClientResponseExtensionsTests {

	@Mock(answer = Answers.RETURNS_MOCKS)
	lateinit var response: ClientResponse

	@Test
	fun `bodyToMono with reified type parameters`() {
		response.bodyToMono<List<Foo>>()
		verify(response, times(1)).bodyToMono(object : ParameterizedTypeReference<List<Foo>>() {})
	}

	@Test
	fun `bodyToFlux with reified type parameters`() {
		response.bodyToFlux<List<Foo>>()
		verify(response, times(1)).bodyToFlux(object : ParameterizedTypeReference<List<Foo>>() {})
	}

	@Test
	fun `toEntity with reified type parameters`() {
		response.toEntity<List<Foo>>()
		verify(response, times(1)).toEntity(object : ParameterizedTypeReference<List<Foo>>() {})
	}

	@Test
	fun `ResponseSpec#toEntityList with reified type parameters`() {
		response.toEntityList<List<Foo>>()
		verify(response, times(1)).toEntityList(object : ParameterizedTypeReference<List<Foo>>() {})
	}

	class Foo
}
