

package org.springframework.http.client;

import org.junit.Test;

import org.springframework.http.HttpMethod;

/**
 * @author Roy Clarkson
 */
public class OkHttp3AsyncClientHttpRequestFactoryTests extends AbstractAsyncHttpRequestFactoryTestCase {

	@SuppressWarnings("deprecation")
	@Override
	protected AsyncClientHttpRequestFactory createRequestFactory() {
		return new OkHttp3ClientHttpRequestFactory();
	}

	@Override
	@Test
	public void httpMethods() throws Exception {
		super.httpMethods();
		assertHttpMethod("patch", HttpMethod.PATCH);
	}

}
