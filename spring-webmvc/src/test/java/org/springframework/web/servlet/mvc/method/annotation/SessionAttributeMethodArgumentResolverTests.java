

package org.springframework.web.servlet.mvc.method.annotation;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

/**
 * Unit tests for {@link SessionAttributeMethodArgumentResolver}.
 *
 *
 * @since 4.3
 */
public class SessionAttributeMethodArgumentResolverTests extends AbstractRequestAttributesArgumentResolverTests {

	@Override
	protected HandlerMethodArgumentResolver createResolver() {
		return new SessionAttributeMethodArgumentResolver();
	}

	@Override
	protected String getHandleMethodName() {
		return "handleWithSessionAttribute";
	}

	@Override
	protected int getScope() {
		return RequestAttributes.SCOPE_SESSION;
	}

}
