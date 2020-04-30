

package org.springframework.web.servlet.config.annotation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.lang.Nullable;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.UrlPathHelper;

/**
 * Helps with configuring HandlerMappings path matching options such as trailing
 * slash match, suffix registration, path matcher and path helper.
 *
 * Configured path matcher and path helper instances are shared for:
 * <ul>
 * <li>RequestMappings</li>
 * <li>ViewControllerMappings</li>
 * <li>ResourcesMappings</li>
 * </ul>
 * @since 4.0.3
 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
 * @see org.springframework.web.servlet.handler.SimpleUrlHandlerMapping
 */
public class PathMatchConfigurer {

	@Nullable
	private Boolean suffixPatternMatch;

	@Nullable
	private Boolean trailingSlashMatch;

	@Nullable
	private Boolean registeredSuffixPatternMatch;

	@Nullable
	private UrlPathHelper urlPathHelper;

	@Nullable
	private PathMatcher pathMatcher;

	@Nullable
	private Map<String, Predicate<Class<?>>> pathPrefixes;

	/**
	 * Whether to use suffix pattern match (".*") when matching patterns to requests. If enabled a method mapped to "/users" also matches to "/users.*".
	 * By default this is set to {@code true}.
	 * @see #registeredSuffixPatternMatch
	 */
	public PathMatchConfigurer setUseSuffixPatternMatch(Boolean suffixPatternMatch) {
		this.suffixPatternMatch = suffixPatternMatch;
		return this;
	}

	/**
	 * Whether to match to URLs irrespective of the presence of a trailing slash.
	 * If enabled a method mapped to "/users" also matches to "/users/".
	 * The default value is {@code true}.
	 */
	public PathMatchConfigurer setUseTrailingSlashMatch(Boolean trailingSlashMatch) {
		this.trailingSlashMatch = trailingSlashMatch;
		return this;
	}

	/**
	 * Whether suffix pattern matching should work only against path extensions
	 * explicitly registered when you
	 * {@link WebMvcConfigurer#configureContentNegotiation configure content
	 * negotiation}. This is generally recommended to reduce ambiguity and to
	 * avoid issues such as when a "." appears in the path for other reasons.
	 * By default this is set to "false".
	 * @see WebMvcConfigurer#configureContentNegotiation
	 */
	public PathMatchConfigurer setUseRegisteredSuffixPatternMatch(Boolean registeredSuffixPatternMatch) {
		this.registeredSuffixPatternMatch = registeredSuffixPatternMatch;
		return this;
	}

	/**
	 * Set the UrlPathHelper to use for resolution of lookup paths.
	 * Use this to override the default UrlPathHelper with a custom subclass,
	 * or to share common UrlPathHelper settings across multiple HandlerMappings and MethodNameResolvers.
	 */
	public PathMatchConfigurer setUrlPathHelper(UrlPathHelper urlPathHelper) {
		this.urlPathHelper = urlPathHelper;
		return this;
	}

	/**
	 * Set the PathMatcher implementation to use for matching URL paths against registered URL patterns. Default is AntPathMatcher.
	 * @see org.springframework.util.AntPathMatcher
	 */
	public PathMatchConfigurer setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
		return this;
	}

	/**
	 * Configure a path prefix to apply to matching controller methods.
	 * Prefixes are used to enrich the mappings of every {@code @RequestMapping}
	 * method whose controller type is matched by the corresponding
	 * {@code Predicate}. The prefix for the first matching predicate is used.
	 * Consider using {@link org.springframework.web.method.HandlerTypePredicate
	 * HandlerTypePredicate} to group controllers.
	 * @param prefix the prefix to apply
	 * @param predicate a predicate for matching controller types
	 * @since 5.1
	 */
	public PathMatchConfigurer addPathPrefix(String prefix, Predicate<Class<?>> predicate) {
		if (this.pathPrefixes == null) this.pathPrefixes = new LinkedHashMap<>();
		this.pathPrefixes.put(prefix, predicate);
		return this;
	}

	@Nullable
	public Boolean isUseSuffixPatternMatch() {
		return this.suffixPatternMatch;
	}

	@Nullable
	public Boolean isUseTrailingSlashMatch() {
		return this.trailingSlashMatch;
	}

	@Nullable
	public Boolean isUseRegisteredSuffixPatternMatch() {
		return this.registeredSuffixPatternMatch;
	}

	@Nullable
	public UrlPathHelper getUrlPathHelper() {
		return this.urlPathHelper;
	}

	@Nullable
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	@Nullable
	protected Map<String, Predicate<Class<?>>> getPathPrefixes() {
		return this.pathPrefixes;
	}
}
