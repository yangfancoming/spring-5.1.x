

package org.springframework.web.method.annotation;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Resolves method arguments annotated with {@code @Value}.
 *
 * An {@code @Value} does not have a name but gets resolved from the default
 * value string, which may contain ${...} placeholder or Spring Expression
 * Language #{...} expressions.
 *
 * A {@link WebDataBinder} may be invoked to apply type conversion to
 * resolved argument value.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public class ExpressionValueMethodArgumentResolver extends AbstractNamedValueMethodArgumentResolver {

	/**
	 * Create a new {@link ExpressionValueMethodArgumentResolver} instance.
	 * @param beanFactory a bean factory to use for resolving  ${...}
	 * placeholder and #{...} SpEL expressions in default values;
	 * or {@code null} if default values are not expected to contain expressions
	 */
	public ExpressionValueMethodArgumentResolver(@Nullable ConfigurableBeanFactory beanFactory) {
		super(beanFactory);
	}


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Value.class);
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		Value ann = parameter.getParameterAnnotation(Value.class);
		Assert.state(ann != null, "No Value annotation");
		return new ExpressionValueNamedValueInfo(ann);
	}

	@Override
	@Nullable
	protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest webRequest) throws Exception {
		// No name to resolve
		return null;
	}

	@Override
	protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
		throw new UnsupportedOperationException("@Value is never required: " + parameter.getMethod());
	}


	private static final class ExpressionValueNamedValueInfo extends NamedValueInfo {

		private ExpressionValueNamedValueInfo(Value annotation) {
			super("@Value", false, annotation.value());
		}
	}

}
