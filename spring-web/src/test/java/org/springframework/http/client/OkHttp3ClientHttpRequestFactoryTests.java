

package org.springframework.http.client;

import org.junit.Test;

import org.springframework.http.HttpMethod;

/**
 * @author Roy Clarkson
 */
public class OkHttp3ClientHttpRequestFactoryTests extends AbstractHttpRequestFactoryTestCase {

	@Override
	protected ClientHttpRequestFactory createRequestFactory() {
		return new OkHttp3ClientHttpRequestFactory();
	}

	@Override
	@Test
	public void httpMethods() throws Exception {
		super.httpMethods();
		assertHttpMethod("patch", HttpMethod.PATCH);
	}

}
