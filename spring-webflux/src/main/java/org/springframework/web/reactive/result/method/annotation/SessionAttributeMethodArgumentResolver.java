

package org.springframework.web.reactive.result.method.annotation;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

/**
 * Resolves method arguments annotated with an @{@link SessionAttribute}.
 *
 *
 * @since 5.0
 * @see RequestAttributeMethodArgumentResolver
 */
public class SessionAttributeMethodArgumentResolver extends AbstractNamedValueArgumentResolver {

	public SessionAttributeMethodArgumentResolver(ConfigurableBeanFactory factory, ReactiveAdapterRegistry registry) {
		super(factory, registry);
	}


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(SessionAttribute.class);
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		SessionAttribute ann = parameter.getParameterAnnotation(SessionAttribute.class);
		Assert.state(ann != null, "No SessionAttribute annotation");
		return new NamedValueInfo(ann.name(), ann.required(), ValueConstants.DEFAULT_NONE);
	}

	@Override
	protected Mono<Object> resolveName(String name, MethodParameter parameter, ServerWebExchange exchange) {
		return exchange.getSession()
				.filter(session -> session.getAttribute(name) != null)
				.map(session -> session.getAttribute(name));
	}

	@Override
	protected void handleMissingValue(String name, MethodParameter parameter) {
		String type = parameter.getNestedParameterType().getSimpleName();
		String reason = "Missing session attribute '" + name + "' of type " + type;
		throw new ServerWebInputException(reason, parameter);
	}

}
