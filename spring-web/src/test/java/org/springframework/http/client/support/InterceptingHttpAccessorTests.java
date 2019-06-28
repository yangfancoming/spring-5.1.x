

package org.springframework.http.client.support;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import static org.junit.Assert.*;

/**
 * Tests for {@link InterceptingHttpAccessor}.
 *
 * @author Brian Clozel
 */
public class InterceptingHttpAccessorTests {

	@Test
	public void getInterceptors() {
		TestInterceptingHttpAccessor accessor = new TestInterceptingHttpAccessor();
		List<ClientHttpRequestInterceptor> interceptors = Arrays.asList(
				new SecondClientHttpRequestInterceptor(),
				new ThirdClientHttpRequestInterceptor(),
				new FirstClientHttpRequestInterceptor()

		);
		accessor.setInterceptors(interceptors);

		assertThat(accessor.getInterceptors().get(0), Matchers.instanceOf(FirstClientHttpRequestInterceptor.class));
		assertThat(accessor.getInterceptors().get(1), Matchers.instanceOf(SecondClientHttpRequestInterceptor.class));
		assertThat(accessor.getInterceptors().get(2), Matchers.instanceOf(ThirdClientHttpRequestInterceptor.class));
	}


	private class TestInterceptingHttpAccessor extends InterceptingHttpAccessor {
	}


	@Order(1)
	private class FirstClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
			return null;
		}
	}


	private class SecondClientHttpRequestInterceptor implements ClientHttpRequestInterceptor, Ordered {

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
			return null;
		}

		@Override
		public int getOrder() {
			return 2;
		}
	}


	private class ThirdClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
			return null;
		}
	}

}
