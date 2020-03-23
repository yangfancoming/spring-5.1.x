

package org.springframework.web.reactive.function.server

import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.reactivestreams.Publisher
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType.*

/**
 * Mock object based tests for [ServerResponse] Kotlin extensions
 *
 * @author Sebastien Deleuze
 */
@RunWith(MockitoJUnitRunner::class)
class ServerResponseExtensionsTests {

	@Mock(answer = Answers.RETURNS_MOCKS)
	lateinit var bodyBuilder: ServerResponse.BodyBuilder


	@Test
	fun `BodyBuilder#body with Publisher and reified type parameters`() {
		val body = mock<Publisher<List<Foo>>>()
		bodyBuilder.body(body)
		verify(bodyBuilder, times(1)).body(body, object : ParameterizedTypeReference<List<Foo>>() {})
	}

	@Test
	fun `BodyBuilder#bodyToServerSentEvents with Publisher and reified type parameters`() {
		val body = mock<Publisher<List<Foo>>>()
		bodyBuilder.bodyToServerSentEvents(body)
		verify(bodyBuilder, times(1)).contentType(TEXT_EVENT_STREAM)
	}

	@Test
	fun `BodyBuilder#json`() {
		bodyBuilder.json()
		verify(bodyBuilder, times(1)).contentType(APPLICATION_JSON_UTF8)
	}

	@Test
	fun `BodyBuilder#xml`() {
		bodyBuilder.xml()
		verify(bodyBuilder, times(1)).contentType(APPLICATION_XML)
	}

	@Test
	fun `BodyBuilder#html`() {
		bodyBuilder.html()
		verify(bodyBuilder, times(1)).contentType(TEXT_HTML)
	}

	class Foo
}
