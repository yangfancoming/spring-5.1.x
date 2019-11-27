

package org.springframework.http.client;

import org.junit.Test;

import org.springframework.http.HttpMethod;


public class StreamingHttpComponentsClientHttpRequestFactoryTests extends AbstractHttpRequestFactoryTestCase {

	@Override
	protected ClientHttpRequestFactory createRequestFactory() {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setBufferRequestBody(false);
		return requestFactory;
	}

	@Override
	@Test
	public void httpMethods() throws Exception {
		assertHttpMethod("patch", HttpMethod.PATCH);
	}

}
