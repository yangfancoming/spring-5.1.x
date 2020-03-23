

package org.springframework.web.reactive.result.method.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.SyncHandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

/**
 * Resolver for a {@link SessionStatus} argument obtaining it from the
 * {@link BindingContext}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class SessionStatusMethodArgumentResolver implements SyncHandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return SessionStatus.class == parameter.getParameterType();
	}

	@Nullable
	@Override
	public Object resolveArgumentValue(
			MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {

		Assert.isInstanceOf(InitBinderBindingContext.class, bindingContext);
		return ((InitBinderBindingContext) bindingContext).getSessionStatus();
	}

}
