

package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

/**
 * Unit tests for {@link RequestAttributeMethodArgumentResolver}.
 *
 *
 * @since 4.3
 */
public class RequestAttributeMethodArgumentResolverTests extends AbstractRequestAttributesArgumentResolverTests {

	@Override
	protected HandlerMethodArgumentResolver createResolver() {
		return new RequestAttributeMethodArgumentResolver();
	}

	@Override
	protected String getHandleMethodName() {
		return "handleWithRequestAttribute";
	}

	@Override
	protected int getScope() {
		return RequestAttributes.SCOPE_REQUEST;
	}

}
