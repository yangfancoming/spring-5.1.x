

package org.springframework.http.client;

import org.junit.Test;

import org.springframework.http.HttpMethod;

/**
 * @author Juergen Hoeller
 */
public class InterceptingStreamingHttpComponentsTests extends AbstractHttpRequestFactoryTestCase {

	@Override
	protected ClientHttpRequestFactory createRequestFactory() {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		requestFactory.setBufferRequestBody(false);
		return new InterceptingClientHttpRequestFactory(requestFactory, null);
	}

	@Override
	@Test
	public void httpMethods() throws Exception {
		assertHttpMethod("patch", HttpMethod.PATCH);
	}

}
