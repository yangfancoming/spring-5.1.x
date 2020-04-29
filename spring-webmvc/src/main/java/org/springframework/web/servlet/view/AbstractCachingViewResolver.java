

package org.springframework.web.servlet.view;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * Convenient base class for {@link org.springframework.web.servlet.ViewResolver} implementations.
 * Caches {@link org.springframework.web.servlet.View} objects
 * once resolved: This means that view resolution won't be a performance problem,no matter how costly initial view retrieval is.
 * Subclasses need to implement the {@link #loadView} template method, building the View object for a specific view name and locale.
 */
public abstract class AbstractCachingViewResolver extends WebApplicationObjectSupport implements ViewResolver {

	/** Default maximum number of entries for the view cache: 1024. */
	public static final int DEFAULT_CACHE_LIMIT = 1024;

	/** Dummy marker object for unresolved views in the cache Maps. */
	private static final View UNRESOLVED_VIEW = new View() {
		@Override
		@Nullable
		public String getContentType() {
			return null;
		}
		@Override
		public void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
		}
	};

	/** The maximum number of entries in the cache. */
	private volatile int cacheLimit = DEFAULT_CACHE_LIMIT;

	/** Whether we should refrain from resolving views again if unresolved once. */
	private boolean cacheUnresolved = true;

	/** Fast access cache for Views, returning already cached instances without a global lock. */
	private final Map<Object, View> viewAccessCache = new ConcurrentHashMap<>(DEFAULT_CACHE_LIMIT);

	/** Map from view key to View instance, synchronized for View creation. */
	@SuppressWarnings("serial")
	private final Map<Object, View> viewCreationCache = new LinkedHashMap<Object, View>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
				@Override
				protected boolean removeEldestEntry(Map.Entry<Object, View> eldest) {
					if (size() > getCacheLimit()) {
						viewAccessCache.remove(eldest.getKey());
						return true;
					}else {
						return false;
					}
				}
			};

	// Specify the maximum number of entries for the view cache.Default is 1024.
	public void setCacheLimit(int cacheLimit) {
		this.cacheLimit = cacheLimit;
	}

	// Return the maximum number of entries for the view cache.
	public int getCacheLimit() {
		return this.cacheLimit;
	}

	/**
	 * Enable or disable caching.
	 * This is equivalent to setting the {@link #setCacheLimit "cacheLimit"}
	 * property to the default limit (1024) or to 0, respectively.
	 * Default is "true": caching is enabled.
	 * Disable this only for debugging and development.
	 */
	public void setCache(boolean cache) {
		this.cacheLimit = (cache ? DEFAULT_CACHE_LIMIT : 0);
	}

	// Return if caching is enabled.
	public boolean isCache() {
		return (this.cacheLimit > 0);
	}

	/**
	 * Whether a view name once resolved to {@code null} should be cached and automatically resolved to {@code null} subsequently.
	 * Default is "true": unresolved view names are being cached, as of Spring 3.1.
	 * Note that this flag only applies if the general {@link #setCache "cache"} flag is kept at its default of "true" as well.
	 * Of specific interest is the ability for some AbstractUrlBasedView implementations (FreeMarker, Tiles) to check if an underlying resource
	 * exists via {@link AbstractUrlBasedView#checkResource(Locale)}.
	 * With this flag set to "false", an underlying resource that re-appears is noticed and used. With the flag set to "true", one check is made only.
	 */
	public void setCacheUnresolved(boolean cacheUnresolved) {
		this.cacheUnresolved = cacheUnresolved;
	}

	//  Return if caching of unresolved views is enabled.
	public boolean isCacheUnresolved() {
		return this.cacheUnresolved;
	}


	@Override
	@Nullable
	public View resolveViewName(String viewName, Locale locale) throws Exception {
		// 判断当前ViewResolver是否设置了需要对需要解析的视图进行缓存，如果不需要缓存，则每次请求时都会重新解析生成视图对象
		if (!isCache()) {
			// 根据视图名称和用户地区信息创建View对象
			return createView(viewName, locale);
		}else {
			// 如果可以对视图进行缓存，则首先获取缓存使用的key，然后从缓存中获取该key，如果没有取到，
			// 则对其进行加锁，再次获取，如果还是没有取到，则创建一个新的View，并且对其进行缓存。
			// 这里使用的是双检查法来判断缓存中是否存在对应的逻辑视图。
			Object cacheKey = getCacheKey(viewName, locale);
			View view = this.viewAccessCache.get(cacheKey);
			if (view == null) {
				synchronized (this.viewCreationCache) {
					view = this.viewCreationCache.get(cacheKey);
					if (view == null) {
						// Ask the subclass to create the View object.
						view = createView(viewName, locale);
						// 这里cacheUnresolved指的是是否缓存默认的空视图，UNRESOLVED_VIEW是一个没有任何内容的View
						if (view == null && this.cacheUnresolved) {
							view = UNRESOLVED_VIEW;
						}
						if (view != null) {
							this.viewAccessCache.put(cacheKey, view);
							this.viewCreationCache.put(cacheKey, view);
						}
					}
				}
			}else {
				if (logger.isTraceEnabled()) logger.trace(formatKey(cacheKey) + "served from cache");
			}
			return (view != UNRESOLVED_VIEW ? view : null);
		}
	}

	private static String formatKey(Object cacheKey) {
		return "View with key [" + cacheKey + "] ";
	}

	/**
	 * Return the cache key for the given view name and the given locale.
	 * Default is a String consisting of view name and locale suffix.
	 * Can be overridden in subclasses.
	 * Needs to respect the locale in general, as a different locale can
	 * lead to a different view resource.
	 */
	protected Object getCacheKey(String viewName, Locale locale) {
		return viewName + '_' + locale;
	}

	/**
	 * Provides functionality to clear the cache for a certain view.
	 * This can be handy in case developer are able to modify views
	 * (e.g. FreeMarker templates) at runtime after which you'd need to
	 * clear the cache for the specified view.
	 * @param viewName the view name for which the cached view object
	 * (if any) needs to be removed
	 * @param locale the locale for which the view object should be removed
	 */
	public void removeFromCache(String viewName, Locale locale) {
		if (!isCache()) {
			logger.warn("Caching is OFF (removal not necessary)");
		}else {
			Object cacheKey = getCacheKey(viewName, locale);
			Object cachedView;
			synchronized (this.viewCreationCache) {
				this.viewAccessCache.remove(cacheKey);
				cachedView = this.viewCreationCache.remove(cacheKey);
			}
			// Some debug output might be useful...
			if (logger.isDebugEnabled()) logger.debug(formatKey(cacheKey) + (cachedView != null ? "cleared from cache" : "not found in the cache"));
		}
	}

	/**
	 * Clear the entire view cache, removing all cached view objects.
	 * Subsequent resolve calls will lead to recreation of demanded view objects.
	 */
	public void clearCache() {
		logger.debug("Clearing all views from the cache");
		synchronized (this.viewCreationCache) {
			this.viewAccessCache.clear();
			this.viewCreationCache.clear();
		}
	}


	/**
	 * Create the actual View object.
	 * The default implementation delegates to {@link #loadView}.
	 * This can be overridden to resolve certain view names in a special fashion,
	 * before delegating to the actual {@code loadView} implementation provided by the subclass.
	 * @param viewName the name of the view to retrieve
	 * @param locale the Locale to retrieve the view for
	 * @return the View instance, or {@code null} if not found (optional, to allow for ViewResolver chaining)
	 * @throws Exception if the view couldn't be resolved
	 * @see #loadView
	 */
	@Nullable
	protected View createView(String viewName, Locale locale) throws Exception {
		return loadView(viewName, locale);
	}

	/**
	 * Subclasses must implement this method, building a View object
	 * for the specified view. The returned View objects will be cached by this ViewResolver base class.
	 * Subclasses are not forced to support internationalization:
	 * A subclass that does not may simply ignore the locale parameter.
	 * @param viewName the name of the view to retrieve
	 * @param locale the Locale to retrieve the view for
	 * @return the View instance, or {@code null} if not found
	 * (optional, to allow for ViewResolver chaining)
	 * @throws Exception if the view couldn't be resolved
	 * @see #resolveViewName
	 */
	@Nullable
	protected abstract View loadView(String viewName, Locale locale) throws Exception;
}
