

package org.springframework.web.reactive.result.method.annotation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolverSupport;
import org.springframework.web.reactive.result.method.SyncHandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

/**
 * Resolves arguments of type {@link Map} annotated with {@link MatrixVariable @MatrixVariable}
 * where the annotation does not specify a name. In other words the purpose of this resolver
 * is to provide access to multiple matrix variables, either all or associated with a specific
 * path variable.
 *
 * When a name is specified, an argument of type Map is considered to be a single attribute
 * with a Map value, and is resolved by {@link MatrixVariableMethodArgumentResolver} instead.
 *
 *
 * @since 5.0.1
 * @see MatrixVariableMethodArgumentResolver
 */
public class MatrixVariableMapMethodArgumentResolver extends HandlerMethodArgumentResolverSupport
		implements SyncHandlerMethodArgumentResolver {

	public MatrixVariableMapMethodArgumentResolver(ReactiveAdapterRegistry registry) {
		super(registry);
	}


	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return checkAnnotatedParamNoReactiveWrapper(parameter, MatrixVariable.class,
				(ann, type) -> (Map.class.isAssignableFrom(type) && !StringUtils.hasText(ann.name())));
	}

	@Nullable
	@Override
	public Object resolveArgumentValue(MethodParameter parameter, BindingContext bindingContext,
			ServerWebExchange exchange) {

		Map<String, MultiValueMap<String, String>> matrixVariables =
				exchange.getAttribute(HandlerMapping.MATRIX_VARIABLES_ATTRIBUTE);

		if (CollectionUtils.isEmpty(matrixVariables)) {
			return Collections.emptyMap();
		}

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		MatrixVariable annotation = parameter.getParameterAnnotation(MatrixVariable.class);
		Assert.state(annotation != null, "No MatrixVariable annotation");
		String pathVariable = annotation.pathVar();

		if (!pathVariable.equals(ValueConstants.DEFAULT_NONE)) {
			MultiValueMap<String, String> mapForPathVariable = matrixVariables.get(pathVariable);
			if (mapForPathVariable == null) {
				return Collections.emptyMap();
			}
			map.putAll(mapForPathVariable);
		}
		else {
			for (MultiValueMap<String, String> vars : matrixVariables.values()) {
				vars.forEach((name, values) -> {
					for (String value : values) {
						map.add(name, value);
					}
				});
			}
		}

		return (isSingleValueMap(parameter) ? map.toSingleValueMap() : map);
	}

	private boolean isSingleValueMap(MethodParameter parameter) {
		if (!MultiValueMap.class.isAssignableFrom(parameter.getParameterType())) {
			ResolvableType[] genericTypes = ResolvableType.forMethodParameter(parameter).getGenerics();
			if (genericTypes.length == 2) {
				return !List.class.isAssignableFrom(genericTypes[1].toClass());
			}
		}
		return false;
	}

}
