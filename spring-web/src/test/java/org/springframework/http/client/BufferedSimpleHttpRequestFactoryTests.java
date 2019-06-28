

package org.springframework.http.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import org.junit.Test;

import org.springframework.http.HttpMethod;

import static org.junit.Assert.*;

public class BufferedSimpleHttpRequestFactoryTests extends AbstractHttpRequestFactoryTestCase {

	@Override
	protected ClientHttpRequestFactory createRequestFactory() {
		return new SimpleClientHttpRequestFactory();
	}

	@Override
	@Test
	public void httpMethods() throws Exception {
		try {
			assertHttpMethod("patch", HttpMethod.PATCH);
		}
		catch (ProtocolException ex) {
			// Currently HttpURLConnection does not support HTTP PATCH
		}
	}

	@Test
	public void prepareConnectionWithRequestBody() throws Exception {
		URL uri = new URL("https://example.com");
		testRequestBodyAllowed(uri, "GET", false);
		testRequestBodyAllowed(uri, "HEAD", false);
		testRequestBodyAllowed(uri, "OPTIONS", false);
		testRequestBodyAllowed(uri, "TRACE", false);
		testRequestBodyAllowed(uri, "PUT", true);
		testRequestBodyAllowed(uri, "POST", true);
		testRequestBodyAllowed(uri, "DELETE", true);
	}

	@Test
	public void deleteWithoutBodyDoesNotRaiseException() throws Exception {
		HttpURLConnection connection = new TestHttpURLConnection(new URL("https://example.com"));
		((SimpleClientHttpRequestFactory) this.factory).prepareConnection(connection, "DELETE");
		SimpleBufferingClientHttpRequest request = new SimpleBufferingClientHttpRequest(connection, false);
		request.execute();
	}

	private void testRequestBodyAllowed(URL uri, String httpMethod, boolean allowed) throws IOException {
		HttpURLConnection connection = new TestHttpURLConnection(uri);
		((SimpleClientHttpRequestFactory) this.factory).prepareConnection(connection, httpMethod);
		assertEquals(allowed, connection.getDoOutput());
	}


	private static class TestHttpURLConnection extends HttpURLConnection {

		public TestHttpURLConnection(URL uri) {
			super(uri);
		}

		@Override
		public void connect() throws IOException {
		}

		@Override
		public void disconnect() {
		}

		@Override
		public boolean usingProxy() {
			return false;
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return new ByteArrayInputStream(new byte[0]);
		}
	}

}
