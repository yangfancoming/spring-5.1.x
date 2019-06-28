

package org.springframework.http.client;

import java.net.ProtocolException;

import org.junit.Test;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.HttpMethod;

public class BufferedSimpleAsyncHttpRequestFactoryTests extends AbstractAsyncHttpRequestFactoryTestCase {

	@SuppressWarnings("deprecation")
	@Override
	protected AsyncClientHttpRequestFactory createRequestFactory() {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		AsyncListenableTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		requestFactory.setTaskExecutor(taskExecutor);
		return requestFactory;
	}

	@Override
	@Test
	public void httpMethods() throws Exception {
		super.httpMethods();
		try {
			assertHttpMethod("patch", HttpMethod.PATCH);
		}
		catch (ProtocolException ex) {
			// Currently HttpURLConnection does not support HTTP PATCH
		}
	}

}
