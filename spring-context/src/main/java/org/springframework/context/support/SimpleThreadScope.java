

package org.springframework.context.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

/**
 * A simple thread-backed {@link Scope} implementation.
 *
 * <p><b>NOTE:</b> This thread scope is not registered by default in common contexts.
 * Instead, you need to explicitly assign it to a scope key in your setup, either through
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory#registerScope}
 * or through a {@link org.springframework.beans.factory.config.CustomScopeConfigurer} bean.
 *
 * <p>{@code SimpleThreadScope} <em>does not clean up any objects</em> associated with it.
 * It is therefore typically preferable to use a request-bound scope implementation such
 * as {@code org.springframework.web.context.request.RequestScope} in web environments,
 * implementing the full lifecycle for scoped attributes (including reliable destruction).
 *
 * <p>For an implementation of a thread-based {@code Scope} with support for destruction
 * callbacks, refer to
 * <a href="https://www.springbyexample.org/examples/custom-thread-scope-module.html">Spring by Example</a>.
 *
 * <p>Thanks to Eugene Kuleshov for submitting the original prototype for a thread scope!
 *
 * @author Arjen Poutsma

 * @since 3.0
 * @see org.springframework.web.context.request.RequestScope
 */
public class SimpleThreadScope implements Scope {

	private static final Log logger = LogFactory.getLog(SimpleThreadScope.class);

	private final ThreadLocal<Map<String, Object>> threadScope =
			new NamedThreadLocal<Map<String, Object>>("SimpleThreadScope") {
				@Override
				protected Map<String, Object> initialValue() {
					return new HashMap<>();
				}
			};


	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		Map<String, Object> scope = this.threadScope.get();
		Object scopedObject = scope.get(name);
		if (scopedObject == null) {
			scopedObject = objectFactory.getObject();
			scope.put(name, scopedObject);
		}
		return scopedObject;
	}

	@Override
	@Nullable
	public Object remove(String name) {
		Map<String, Object> scope = this.threadScope.get();
		return scope.remove(name);
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		logger.warn("SimpleThreadScope does not support destruction callbacks. " +
				"Consider using RequestScope in a web environment.");
	}

	@Override
	@Nullable
	public Object resolveContextualObject(String key) {
		return null;
	}

	@Override
	public String getConversationId() {
		return Thread.currentThread().getName();
	}

}
