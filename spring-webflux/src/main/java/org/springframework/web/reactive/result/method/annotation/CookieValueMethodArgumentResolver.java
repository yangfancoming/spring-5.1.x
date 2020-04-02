

package org.springframework.web.reactive.result.method.annotation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.http.HttpCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

/**
 * Resolve method arguments annotated with {@code @CookieValue}.
 *
 * An {@code @CookieValue} is a named value that is resolved from a cookie.
 * It has a required flag and a default value to fall back on when the cookie
 * does not exist.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class CookieValueMethodArgumentResolver extends AbstractNamedValueSyncArgumentResolver {

	/**
	 * Create a new {@link CookieValueMethodArgumentResolver} instance.
	 * @param factory a bean factory to use for resolving {@code ${...}}
	 * placeholder and {@code #{...}} SpEL expressions in default values;
	 * or {@code null} if default values are not expected to contain expressions
	 * @param registry for checking reactive type wrappers
	 */
	public CookieValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory factory,
			ReactiveAdapterRegistry registry) {

		super(factory, registry);
	}


	@Override
	public boolean supportsParameter(MethodParameter param) {
		return checkAnnotatedParamNoReactiveWrapper(param, CookieValue.class, (annot, type) -> true);
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		CookieValue ann = parameter.getParameterAnnotation(CookieValue.class);
		Assert.state(ann != null, "No CookieValue annotation");
		return new CookieValueNamedValueInfo(ann);
	}

	@Override
	protected Object resolveNamedValue(String name, MethodParameter parameter, ServerWebExchange exchange) {
		HttpCookie cookie = exchange.getRequest().getCookies().getFirst(name);
		Class<?> paramType = parameter.getNestedParameterType();
		if (HttpCookie.class.isAssignableFrom(paramType)) {
			return cookie;
		}
		return (cookie != null ? cookie.getValue() : null);
	}

	@Override
	protected void handleMissingValue(String name, MethodParameter parameter) {
		String type = parameter.getNestedParameterType().getSimpleName();
		String reason = "Missing cookie '" + name + "' for method parameter of type " + type;
		throw new ServerWebInputException(reason, parameter);
	}


	private static final class CookieValueNamedValueInfo extends NamedValueInfo {

		private CookieValueNamedValueInfo(CookieValue annotation) {
			super(annotation.name(), annotation.required(), annotation.defaultValue());
		}
	}

}
