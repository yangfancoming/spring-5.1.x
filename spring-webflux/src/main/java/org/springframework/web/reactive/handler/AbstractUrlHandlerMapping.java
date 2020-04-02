

package org.springframework.web.reactive.handler;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import reactor.core.publisher.Mono;

import org.springframework.beans.BeansException;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;

/**
 * Abstract base class for URL-mapped
 * {@link org.springframework.web.reactive.HandlerMapping} implementations.
 *
 * Supports direct matches, e.g. a registered "/test" matches "/test", and
 * various path pattern matches, e.g. a registered "/t*" pattern matches
 * both "/test" and "/team", "/test/*" matches all paths under "/test",
 * "/test/**" matches all paths below "/test". For details, see the
 * {@link org.springframework.web.util.pattern.PathPattern} javadoc.
 *
 * Will search all path patterns to find the most specific match for the
 * current request path. The most specific pattern is defined as the longest
 * path pattern with the fewest captured variables and wildcards.
 * @since 5.0
 *
 * 本文分析了SimpleUrlHandlerMapping类初始化过程，其实核心就是把url和handler进行了映射，供后续访问使用，单靠看文章无法掌握。
 * 整个过程调用很复杂，大家多debug跟踪，一定能了解其内部的逻辑。
 */
public abstract class AbstractUrlHandlerMapping extends AbstractHandlerMapping {

	private boolean lazyInitHandlers = false;

	private final Map<PathPattern, Object> handlerMap = new LinkedHashMap<>();


	/**
	 * Set whether to lazily initialize handlers. Only applicable to
	 * singleton handlers, as prototypes are always lazily initialized.
	 * Default is "false", as eager initialization allows for more efficiency
	 * through referencing the controller objects directly.
	 * If you want to allow your controllers to be lazily initialized,
	 * make them "lazy-init" and set this flag to true. Just making them
	 * "lazy-init" will not work, as they are initialized through the
	 * references from the handler mapping in this case.
	 */
	public void setLazyInitHandlers(boolean lazyInitHandlers) {
		this.lazyInitHandlers = lazyInitHandlers;
	}

	/**
	 * Return a read-only view of registered path patterns and handlers which may
	 * may be an actual handler instance or the bean name of lazily initialized
	 * handler.
	 */
	public final Map<PathPattern, Object> getHandlerMap() {
		return Collections.unmodifiableMap(this.handlerMap);
	}


	@Override
	public Mono<Object> getHandlerInternal(ServerWebExchange exchange) {
		PathContainer lookupPath = exchange.getRequest().getPath().pathWithinApplication();
		Object handler;
		try {
			handler = lookupHandler(lookupPath, exchange);
		}
		catch (Exception ex) {
			return Mono.error(ex);
		}
		return Mono.justOrEmpty(handler);
	}

	/**
	 * Look up a handler instance for the given URL lookup path.
	 * Supports direct matches, e.g. a registered "/test" matches "/test",
	 * and various path pattern matches, e.g. a registered "/t*" matches
	 * both "/test" and "/team". For details, see the PathPattern class.
	 * @param lookupPath the URL the handler is mapped to
	 * @param exchange the current exchange
	 * @return the associated handler instance, or {@code null} if not found
	 * @see org.springframework.web.util.pattern.PathPattern
	 */
	@Nullable
	protected Object lookupHandler(PathContainer lookupPath, ServerWebExchange exchange) throws Exception {

		List<PathPattern> matches = this.handlerMap.keySet().stream()
				.filter(key -> key.matches(lookupPath))
				.collect(Collectors.toList());

		if (matches.isEmpty()) {
			return null;
		}

		if (matches.size() > 1) {
			matches.sort(PathPattern.SPECIFICITY_COMPARATOR);
			if (logger.isTraceEnabled()) {
				logger.debug(exchange.getLogPrefix() + "Matching patterns " + matches);
			}
		}

		PathPattern pattern = matches.get(0);
		PathContainer pathWithinMapping = pattern.extractPathWithinPattern(lookupPath);
		return handleMatch(this.handlerMap.get(pattern), pattern, pathWithinMapping, exchange);
	}

	private Object handleMatch(Object handler, PathPattern bestMatch, PathContainer pathWithinMapping,
			ServerWebExchange exchange) {

		// Bean name or resolved handler?
		if (handler instanceof String) {
			String handlerName = (String) handler;
			handler = obtainApplicationContext().getBean(handlerName);
		}

		validateHandler(handler, exchange);

		exchange.getAttributes().put(BEST_MATCHING_HANDLER_ATTRIBUTE, handler);
		exchange.getAttributes().put(BEST_MATCHING_PATTERN_ATTRIBUTE, bestMatch);
		exchange.getAttributes().put(PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, pathWithinMapping);

		return handler;
	}

	/**
	 * Validate the given handler against the current request.
	 * The default implementation is empty. Can be overridden in subclasses,
	 * for example to enforce specific preconditions expressed in URL mappings.
	 * @param handler the handler object to validate
	 * @param exchange current exchange
	 */
	@SuppressWarnings("UnusedParameters")
	protected void validateHandler(Object handler, ServerWebExchange exchange) {
	}

	/**
	 * Register the specified handler for the given URL paths.
	 * @param urlPaths the URLs that the bean should be mapped to
	 * @param beanName the name of the handler bean
	 * @throws BeansException if the handler couldn't be registered
	 * @throws IllegalStateException if there is a conflicting handler registered
	 */
	protected void registerHandler(String[] urlPaths, String beanName) throws BeansException, IllegalStateException {
		Assert.notNull(urlPaths, "URL path array must not be null");
		for (String urlPath : urlPaths) {
			registerHandler(urlPath, beanName);
		}
	}

	/**
	 * Register the specified handler for the given URL path.
	 * @param urlPath the URL the bean should be mapped to
	 * @param handler the handler instance or handler bean name String
	 * (a bean name will automatically be resolved into the corresponding handler bean)
	 * @throws BeansException if the handler couldn't be registered
	 * @throws IllegalStateException if there is a conflicting handler registered
	 */
	protected void registerHandler(String urlPath, Object handler) throws BeansException, IllegalStateException {
		Assert.notNull(urlPath, "URL path must not be null");
		Assert.notNull(handler, "Handler object must not be null");
		Object resolvedHandler = handler;

		// Parse path pattern
		urlPath = prependLeadingSlash(urlPath);
		PathPattern pattern = getPathPatternParser().parse(urlPath);
		if (this.handlerMap.containsKey(pattern)) {
			Object existingHandler = this.handlerMap.get(pattern);
			if (existingHandler != null && existingHandler != resolvedHandler) {
				throw new IllegalStateException("Cannot map " + getHandlerDescription(handler) + " to [" + urlPath + "]: " + "there is already " + getHandlerDescription(existingHandler) + " mapped.");
			}
		}

		/**
		 // 不是懒加载，默认为false，即不是，通过配置SimpleUrlHandlerMapping属性lazyInitHandlers的值进行控制
		 // 如果不是懒加载并且handler为单例，即从上下文中查询实例处理，此时resolvedHandler为handler实例对象；
		 // 如果是懒加载或者handler不是单例，即resolvedHandler为handler逻辑名
		*/
		// Eagerly resolve handler if referencing singleton via name.
		if (!this.lazyInitHandlers && handler instanceof String) {
			String handlerName = (String) handler;
			if (obtainApplicationContext().isSingleton(handlerName)) {
				resolvedHandler = obtainApplicationContext().getBean(handlerName);
			}
		}
		// Register resolved handler
		this.handlerMap.put(pattern, resolvedHandler);
		if (logger.isTraceEnabled()) {
			logger.trace("Mapped [" + urlPath + "] onto " + getHandlerDescription(handler));
		}
	}

	private String getHandlerDescription(Object handler) {
		return (handler instanceof String ? "'" + handler + "'" : handler.toString());
	}


	private static String prependLeadingSlash(String pattern) {
		if (StringUtils.hasLength(pattern) && !pattern.startsWith("/")) {
			return "/" + pattern;
		}
		else {
			return pattern;
		}
	}

}
