

package org.springframework.web.filter.reactive;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import reactor.core.publisher.Mono;

import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

/**
 * Reactive {@link WebFilter} that converts posted method parameters into HTTP methods,
 * retrievable via {@link ServerHttpRequest#getMethod()}. Since browsers currently only
 * support GET and POST, a common technique is to use a normal POST with an additional
 * hidden form field ({@code _method}) to pass the "real" HTTP method along.
 * This filter reads that parameter and changes the {@link ServerHttpRequest#getMethod()}
 * return value using {@link ServerWebExchange#mutate()}.
 *
 * <p>The name of the request parameter defaults to {@code _method}, but can be
 * adapted via the {@link #setMethodParamName(String) methodParamName} property.
 *
 * @author Greg Turnquist
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class HiddenHttpMethodFilter implements WebFilter {

	private static final List<HttpMethod> ALLOWED_METHODS =
			Collections.unmodifiableList(Arrays.asList(HttpMethod.PUT,
					HttpMethod.DELETE, HttpMethod.PATCH));

	/** Default name of the form parameter with the HTTP method to use. */
	public static final String DEFAULT_METHOD_PARAMETER_NAME = "_method";


	private String methodParamName = DEFAULT_METHOD_PARAMETER_NAME;


	/**
	 * Set the name of the form parameter with the HTTP method to use.
	 * <p>By default this is set to {@code "_method"}.
	 */
	public void setMethodParamName(String methodParamName) {
		Assert.hasText(methodParamName, "'methodParamName' must not be empty");
		this.methodParamName = methodParamName;
	}


	/**
	 * Transform an HTTP POST into another method based on {@code methodParamName}.
	 * @param exchange the current server exchange
	 * @param chain provides a way to delegate to the next filter
	 * @return {@code Mono<Void>} to indicate when request processing is complete
	 */
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		if (exchange.getRequest().getMethod() != HttpMethod.POST) {
			return chain.filter(exchange);
		}

		return exchange.getFormData()
				.map(formData -> {
					String method = formData.getFirst(this.methodParamName);
					return StringUtils.hasLength(method) ? mapExchange(exchange, method) : exchange;
				})
				.flatMap(chain::filter);
	}

	private ServerWebExchange mapExchange(ServerWebExchange exchange, String methodParamValue) {
		HttpMethod httpMethod = HttpMethod.resolve(methodParamValue.toUpperCase(Locale.ENGLISH));
		Assert.notNull(httpMethod, () -> "HttpMethod '" + methodParamValue + "' not supported");
		if (ALLOWED_METHODS.contains(httpMethod)) {
			return exchange.mutate().request(builder -> builder.method(httpMethod)).build();
		}
		else {
			return exchange;
		}
	}

}
