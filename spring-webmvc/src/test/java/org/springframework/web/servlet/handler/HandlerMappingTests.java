

package org.springframework.web.servlet.handler;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.support.WebContentGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Unit tests for {@link org.springframework.web.servlet.handler.HandlerMappingTests}.
 */
public class HandlerMappingTests {

	private MockHttpServletRequest request;
	private AbstractHandlerMapping handlerMapping;
	private StaticWebApplicationContext context;

	@Before
	public void setup() {
		this.context = new StaticWebApplicationContext();
		this.handlerMapping = new TestHandlerMapping();
		this.request = new MockHttpServletRequest();
	}

	@Test
	public void orderedInterceptors()  {
		HandlerInterceptor i1 = Mockito.mock(HandlerInterceptor.class);
		MappedInterceptor mappedInterceptor1 = new MappedInterceptor(new String[]{"/**"}, i1);
		HandlerInterceptor i2 = Mockito.mock(HandlerInterceptor.class);
		HandlerInterceptor i3 = Mockito.mock(HandlerInterceptor.class);
		MappedInterceptor mappedInterceptor3 = new MappedInterceptor(new String[]{"/**"}, i3);
		HandlerInterceptor i4 = Mockito.mock(HandlerInterceptor.class);

		this.handlerMapping.setInterceptors(mappedInterceptor1, i2, mappedInterceptor3, i4);
		this.handlerMapping.setApplicationContext(this.context);
		HandlerExecutionChain chain = this.handlerMapping.getHandlerExecutionChain(new SimpleHandler(), this.request);
		Assert.assertThat(chain.getInterceptors(), Matchers.arrayContaining(mappedInterceptor1.getInterceptor(), i2, mappedInterceptor3.getInterceptor(), i4));
	}

	class TestHandlerMapping extends AbstractHandlerMapping {
		@Override
		protected Object getHandlerInternal(HttpServletRequest request)  {
			return new SimpleHandler();
		}
	}

	class SimpleHandler extends WebContentGenerator implements HttpRequestHandler {
		public SimpleHandler() {
			super(METHOD_GET);
		}
		@Override
		public void handleRequest(HttpServletRequest request, HttpServletResponse response) {
			response.setStatus(HttpStatus.OK.value());
		}

	}

}
