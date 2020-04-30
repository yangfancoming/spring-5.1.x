

package org.springframework.web.method.support;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * Stub resolver for a fixed value type and/or value.
 *
 *
 */
public class StubArgumentResolver implements HandlerMethodArgumentResolver {

	private final Class<?> valueType;

	@Nullable
	private final Object value;

	private List<MethodParameter> resolvedParameters = new ArrayList<>();


	public StubArgumentResolver(Object value) {
		this(value.getClass(), value);
	}

	public StubArgumentResolver(Class<?> valueType) {
		this(valueType, null);
	}

	public StubArgumentResolver(Class<?> valueType, Object value) {
		this.valueType = valueType;
		this.value = value;
	}


	public List<MethodParameter> getResolvedParameters() {
		return resolvedParameters;
	}


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(this.valueType);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

		this.resolvedParameters.add(parameter);
		return this.value;
	}

}
