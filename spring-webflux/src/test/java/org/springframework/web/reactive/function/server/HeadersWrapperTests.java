

package org.springframework.web.reactive.function.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import org.junit.Before;
import org.junit.Test;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.support.ServerRequestWrapper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class HeadersWrapperTests {

	private ServerRequest.Headers mockHeaders;

	private ServerRequestWrapper.HeadersWrapper wrapper;


	@Before
	public void createWrapper() {
		mockHeaders = mock(ServerRequest.Headers.class);
		wrapper = new ServerRequestWrapper.HeadersWrapper(mockHeaders);
	}


	@Test
	public void accept() {
		List<MediaType> accept = Collections.singletonList(MediaType.APPLICATION_JSON);
		when(mockHeaders.accept()).thenReturn(accept);

		assertSame(accept, wrapper.accept());
	}

	@Test
	public void acceptCharset() {
		List<Charset> acceptCharset = Collections.singletonList(StandardCharsets.UTF_8);
		when(mockHeaders.acceptCharset()).thenReturn(acceptCharset);

		assertSame(acceptCharset, wrapper.acceptCharset());
	}

	@Test
	public void contentLength() {
		OptionalLong contentLength = OptionalLong.of(42L);
		when(mockHeaders.contentLength()).thenReturn(contentLength);

		assertSame(contentLength, wrapper.contentLength());
	}

	@Test
	public void contentType() {
		Optional<MediaType> contentType = Optional.of(MediaType.APPLICATION_JSON);
		when(mockHeaders.contentType()).thenReturn(contentType);

		assertSame(contentType, wrapper.contentType());
	}

	@Test
	public void host() {
		InetSocketAddress host = InetSocketAddress.createUnresolved("example.com", 42);
		when(mockHeaders.host()).thenReturn(host);

		assertSame(host, wrapper.host());
	}

	@Test
	public void range() {
		List<HttpRange> range = Collections.singletonList(HttpRange.createByteRange(42));
		when(mockHeaders.range()).thenReturn(range);

		assertSame(range, wrapper.range());
	}

	@Test
	public void header() {
		String name = "foo";
		List<String> value = Collections.singletonList("bar");
		when(mockHeaders.header(name)).thenReturn(value);

		assertSame(value, wrapper.header(name));
	}

	@Test
	public void asHttpHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		when(mockHeaders.asHttpHeaders()).thenReturn(httpHeaders);

		assertSame(httpHeaders, wrapper.asHttpHeaders());
	}

}
