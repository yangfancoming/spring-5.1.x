

package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Contains and delegates calls to a {@link HandlerInterceptor} along with include (and optionally exclude) path patterns to which the interceptor should apply.
 * Also provides matching logic to test if the interceptor applies to a given request path.
 * A MappedInterceptor can be registered directly with any {@link org.springframework.web.servlet.handler.AbstractHandlerMethodMapping}.
 * Furthermore, beans of type {@code MappedInterceptor} are automatically detected by {@code AbstractHandlerMethodMapping} (including ancestor ApplicationContext's) which
 * effectively means the interceptor is registered "globally" with all handler mappings.
 * @since 3.0
 */
public final class MappedInterceptor implements HandlerInterceptor {

	// 包含 正则匹配
	@Nullable
	private final String[] includePatterns;

	// 排除 正则匹配
	@Nullable
	private final String[] excludePatterns;

	// controller 拦截器
	private final HandlerInterceptor interceptor;

	@Nullable
	private PathMatcher pathMatcher;

	/**
	 * Create a new MappedInterceptor instance.
	 * @param includePatterns the path patterns to map (empty for matching to all paths)
	 * @param interceptor the HandlerInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(@Nullable String[] includePatterns, HandlerInterceptor interceptor) {
		this(includePatterns, null, interceptor);
	}

	/**
	 * Create a new MappedInterceptor instance.
	 * @param includePatterns the path patterns to map (empty for matching to all paths)
	 * @param interceptor the WebRequestInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(@Nullable String[] includePatterns, WebRequestInterceptor interceptor) {
		this(includePatterns, null, interceptor);
	}

	/**
	 * Create a new MappedInterceptor instance.
	 * @param includePatterns the path patterns to map (empty for matching to all paths)
	 * @param excludePatterns the path patterns to exclude (empty for no specific excludes)
	 * @param interceptor the WebRequestInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns,WebRequestInterceptor interceptor) {
		this(includePatterns, excludePatterns, new WebRequestHandlerInterceptorAdapter(interceptor));
	}

	/**
	 * Create a new MappedInterceptor instance.
	 * @param includePatterns the path patterns to map (empty for matching to all paths)
	 * @param excludePatterns the path patterns to exclude (empty for no specific excludes)
	 * @param interceptor the HandlerInterceptor instance to map to the given patterns
	 */
	public MappedInterceptor(@Nullable String[] includePatterns, @Nullable String[] excludePatterns,HandlerInterceptor interceptor) {
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
		this.interceptor = interceptor;
	}
	/**
	 * Configure a PathMatcher to use with this MappedInterceptor instead of the one passed by default to the {@link #matches(String, org.springframework.util.PathMatcher)} method.
	 * This is an advanced property that is only required when using custom PathMatcher  implementations that support mapping metadata other than the Ant-style path patterns supported by default.
	 */
	public void setPathMatcher(@Nullable PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}

	/**
	 * The configured PathMatcher, or {@code null} if none.
	 */
	@Nullable
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	/**
	 * The path into the application the interceptor is mapped to.
	 */
	@Nullable
	public String[] getPathPatterns() {
		return this.includePatterns;
	}

	/**
	 * The actual {@link HandlerInterceptor} reference.
	 */
	public HandlerInterceptor getInterceptor() {
		return this.interceptor;
	}

	/**
	 * Determine a match for the given lookup path.
	 * @param lookupPath the current request path  当前请求路径
	 * @param pathMatcher a path matcher for path pattern matching  controller中注册的路径
	 * @return {@code true} if the interceptor applies to the given request path
	 */
	public boolean matches(String lookupPath, PathMatcher pathMatcher) {
		// 若本实例中的pathMatcher属性不为空，则永远使用本实例中的pathMatcher
		PathMatcher pathMatcherToUse = (this.pathMatcher != null ? this.pathMatcher : pathMatcher);
		// 这里必须要先判断 【排除】   其优先级要高于【包含】
		if (!ObjectUtils.isEmpty(this.excludePatterns)) {
			for (String pattern : this.excludePatterns) {
				if (pathMatcherToUse.match(pattern, lookupPath)) return false;
			}
		}
		if (ObjectUtils.isEmpty(this.includePatterns)) return true;
		for (String pattern : this.includePatterns) {
			if (pathMatcherToUse.match(pattern, lookupPath)) return true;
		}
		return false;
	}

	//---------------------------------------------------------------------
	// Implementation of 【HandlerInterceptor】 interface
	//---------------------------------------------------------------------
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception {
		return this.interceptor.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,@Nullable ModelAndView modelAndView) throws Exception {
		this.interceptor.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,@Nullable Exception ex) throws Exception {
		this.interceptor.afterCompletion(request, response, handler, ex);
	}
}
