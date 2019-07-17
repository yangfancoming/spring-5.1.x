

package org.springframework.http.client;

import java.net.HttpURLConnection;

import org.junit.Test;

import org.springframework.http.HttpHeaders;

import static org.mockito.Mockito.*;


public class SimpleClientHttpRequestFactoryTests {

	@Test  // SPR-13225
	public void headerWithNullValue() {
		HttpURLConnection urlConnection = mock(HttpURLConnection.class);
		HttpHeaders headers = new HttpHeaders();
		headers.set("foo", null);
		SimpleBufferingClientHttpRequest.addHeaders(urlConnection, headers);
		verify(urlConnection, times(1)).addRequestProperty("foo", "");
	}

}
