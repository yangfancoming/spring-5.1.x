

package org.springframework.web.reactive.handler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of the {@link org.springframework.web.reactive.HandlerMapping}
 * interface to map from URLs to request handler beans. Supports both mapping
 * to bean instances and mapping to bean names; the latter is required for
 * non-singleton handlers.
 *
 * The "urlMap" property is suitable for populating the handler map with
 * bean instances. Mappings to bean names can be set via the "mappings"
 * property, in a form accepted by the {@code java.util.Properties} class,
 * like as follows:
 *
 * <pre>
 * /welcome.html=ticketController
 * /show.html=ticketController
 * </pre>
 *
 * The syntax is {@code PATH=HANDLER_BEAN_NAME}. If the path doesn't begin
 * with a slash, one is prepended.
 *
 * Supports direct matches, e.g. a registered "/test" matches "/test", and
 * various Ant-style pattern matches, e.g. a registered "/t*" pattern matches
 * both "/test" and "/team", "/test/*" matches all paths under "/test",
 * "/test/**" matches all paths below "/test". For details, see the
 * {@link org.springframework.web.util.pattern.PathPattern} javadoc.

 * @since 5.0
 */
public class SimpleUrlHandlerMapping extends AbstractUrlHandlerMapping {
	// 存储url和bean映射
	private final Map<String, Object> urlMap = new LinkedHashMap<>();

	/**
	 * Map URL paths to handler bean names.
	 * This is the typical way of configuring this HandlerMapping.
	 * Supports direct URL matches and Ant-style pattern matches. For syntax details,
	 * see the {@link org.springframework.web.util.pattern.PathPattern} javadoc.
	 * @param mappings properties with URLs as keys and bean names as values
	 * @see #setUrlMap
	 */
	public void setMappings(Properties mappings) {
		CollectionUtils.mergePropertiesIntoMap(mappings, this.urlMap);
	}

	/**
	 * Set a Map with URL paths as keys and handler beans (or handler bean names)
	 * as values. Convenient for population with bean references.
	 * Supports direct URL matches and Ant-style pattern matches. For syntax details,
	 * see the {@link org.springframework.web.util.pattern.PathPattern} javadoc.
	 * @param urlMap map with URLs as keys and beans as values
	 * @see #setMappings
	 * // 注入property的name为mappings映射
	 */
	public void setUrlMap(Map<String, ?> urlMap) {
		this.urlMap.putAll(urlMap);
	}

	/**
	 * Allow Map access to the URL path mappings, with the option to add or
	 * override specific entries.
	 * Useful for specifying entries directly, for example via "urlMap[myKey]".
	 * This is particularly useful for adding or overriding entries in child
	 * bean definitions.
	 */
	public Map<String, ?> getUrlMap() {
		return this.urlMap;
	}


	/**
	 * Calls the {@link #registerHandlers} method in addition to the superclass's initialization.
	 *   // 实例化本类实例入口
	 */
	@Override
	public void initApplicationContext() throws BeansException {
		// 调用父类AbstractHandlerMapping的initApplicationContext方法，只要完成拦截器的注册
		super.initApplicationContext();
		// 处理url和bean name，具体注册调用父类完成
		registerHandlers(this.urlMap);
	}

	/**
	 * Register all handlers specified in the URL map for the corresponding paths.
	 * @param urlMap a Map with URL paths as keys and handler beans or bean names as values
	 * @throws BeansException if a handler couldn't be registered
	 * @throws IllegalStateException if there is a conflicting handler registered
	 *   // 注册映射关系，及将property中的值解析到map对象中，key为url，value为bean id或name
	 */
	protected void registerHandlers(Map<String, Object> urlMap) throws BeansException {
		if (urlMap.isEmpty()) {
			logger.trace("No patterns in " + formatMappingName());
		}
		else {
			for (Map.Entry<String, Object> entry : urlMap.entrySet()) {
				String url = entry.getKey();
				Object handler = entry.getValue();
				// Prepend with slash if not already present.  增加以"/"开头
				if (!url.startsWith("/")) {
					url = "/" + url;
				}
				// Remove whitespace from handler bean name. // 去除handler bean名称的空格
				if (handler instanceof String) {
					handler = ((String) handler).trim();
				}
				// 调用父类AbstractUrlHandlerMapping完成映射
				registerHandler(url, handler);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Patterns " + getHandlerMap().keySet() + " in " + formatMappingName());
			}
		}
	}

}
