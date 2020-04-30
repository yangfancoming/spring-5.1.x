

package org.springframework.web.reactive.result.method.annotation;

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.ui.Model;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolverSupport;
import org.springframework.web.reactive.result.method.SyncHandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

/**
 * Resolver for a controller method argument of type {@link Model} that can
 * also be resolved as a {@link java.util.Map}.
 *
 *
 * @since 5.0
 */
public class ModelArgumentResolver extends HandlerMethodArgumentResolverSupport
		implements SyncHandlerMethodArgumentResolver {

	public ModelArgumentResolver(ReactiveAdapterRegistry adapterRegistry) {
		super(adapterRegistry);
	}


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return checkParameterTypeNoReactiveWrapper(parameter,
				type -> Model.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type));
	}

	@Override
	public Object resolveArgumentValue(
			MethodParameter parameter, BindingContext context, ServerWebExchange exchange) {

		Class<?> type = parameter.getParameterType();
		if (Model.class.isAssignableFrom(type)) {
			return context.getModel();
		}
		else if (Map.class.isAssignableFrom(type)) {
			return context.getModel().asMap();
		}
		else {
			// Should never happen..
			throw new IllegalStateException("Unexpected method parameter type: " + type);
		}
	}

}
