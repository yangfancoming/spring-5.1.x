

package org.springframework.web.method.annotation;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;

/**
 * A base abstract class to resolve method arguments annotated with
 * {@code @CookieValue}. Subclasses extract the cookie value from the request.
 *
 * An {@code @CookieValue} is a named value that is resolved from a cookie.
 * It has a required flag and a default value to fall back on when the cookie
 * does not exist.
 *
 * A {@link WebDataBinder} may be invoked to apply type conversion to the
 * resolved cookie value.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public abstract class AbstractCookieValueMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

	/**
	 * Crate a new {@link AbstractCookieValueMethodArgumentResolver} instance.
	 * @param beanFactory a bean factory to use for resolving  ${...}
	 * placeholder and #{...} SpEL expressions in default values;
	 * or {@code null} if default values are not expected to contain expressions
	 */
	public AbstractCookieValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
		super(beanFactory);
	}


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CookieValue.class);
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		CookieValue annotation = parameter.getParameterAnnotation(CookieValue.class);
		Assert.state(annotation != null, "No CookieValue annotation");
		return new CookieValueNamedValueInfo(annotation);
	}

	@Override
	protected void handleMissingValue(String name, MethodParameter parameter) throws ServletRequestBindingException {
		throw new MissingRequestCookieException(name, parameter);
	}


	private static final class CookieValueNamedValueInfo extends NamedValueInfo {

		private CookieValueNamedValueInfo(CookieValue annotation) {
			super(annotation.name(), annotation.required(), annotation.defaultValue());
		}
	}

}
