

package org.springframework.web.reactive;

import reactor.core.publisher.Mono;

import org.springframework.web.server.ServerWebExchange;

/**
 * Interface to be implemented by objects that define a mapping between requests and handler objects.
 * 由定义请求和处理程序对象之间映射的对象实现的接口。
 * @since 5.0
 */
public interface HandlerMapping {

	/**
	 * Name of the {@link ServerWebExchange#getAttributes() attribute} that
	 * contains the mapped handler for the best matching pattern.
	 */
	String BEST_MATCHING_HANDLER_ATTRIBUTE = HandlerMapping.class.getName() + ".bestMatchingHandler";

	/**
	 * Name of the {@link ServerWebExchange#getAttributes() attribute} that
	 * contains the best matching pattern within the handler mapping.
	 */
	String BEST_MATCHING_PATTERN_ATTRIBUTE = HandlerMapping.class.getName() + ".bestMatchingPattern";

	/**
	 * Name of the {@link ServerWebExchange#getAttributes() attribute} that
	 * contains the path within the handler mapping, in case of a pattern match
	 * such as {@code "/static/**"} or the full relevant URI otherwise.
	 * Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * typically support it but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 */
	String PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE = HandlerMapping.class.getName() + ".pathWithinHandlerMapping";

	/**
	 * Name of the {@link ServerWebExchange#getAttributes() attribute} that
	 * contains the URI templates map mapping variable names to values.
	 * Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. URL-based HandlerMappings will
	 * typically support it, but handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 */
	String URI_TEMPLATE_VARIABLES_ATTRIBUTE = HandlerMapping.class.getName() + ".uriTemplateVariables";

	/**
	 * Name of the {@link ServerWebExchange#getAttributes() attribute} that
	 * contains a map with URI variable names and a corresponding MultiValueMap
	 * of URI matrix variables for each.
	 * Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations and may also not be present depending on
	 * whether the HandlerMapping is configured to keep matrix variable content
	 * in the request URI.
	 */
	String MATRIX_VARIABLES_ATTRIBUTE = HandlerMapping.class.getName() + ".matrixVariables";

	/**
	 * Name of the {@link ServerWebExchange#getAttributes() attribute} containing
	 * the set of producible MediaType's applicable to the mapped handler.
	 * Note: This attribute is not required to be supported by all
	 * HandlerMapping implementations. Handlers should not necessarily expect
	 * this request attribute to be present in all scenarios.
	 */
	String PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE = HandlerMapping.class.getName() + ".producibleMediaTypes";


	/**
	 * Return a handler for this request.
	 * @param exchange current server exchange
	 * @return a {@link Mono} that emits one value or none in case the request
	 * cannot be resolved to a handler
	 */
	Mono<Object> getHandler(ServerWebExchange exchange);

}
