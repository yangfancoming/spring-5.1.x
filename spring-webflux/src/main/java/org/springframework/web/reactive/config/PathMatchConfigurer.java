

package org.springframework.web.reactive.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.lang.Nullable;

/**
 * Assist with configuring {@code HandlerMapping}'s with path matching options.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @since 5.0
 */
public class PathMatchConfigurer {

	@Nullable
	private Boolean trailingSlashMatch;


	@Nullable
	private Boolean caseSensitiveMatch;

	@Nullable
	private Map<String, Predicate<Class<?>>> pathPrefixes;


	/**
	 * Whether to match to URLs irrespective of their case.
	 * If enabled a method mapped to "/users" won't match to "/Users/".
	 * The default value is {@code false}.
	 */
	public PathMatchConfigurer setUseCaseSensitiveMatch(Boolean caseSensitiveMatch) {
		this.caseSensitiveMatch = caseSensitiveMatch;
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
	 * Configure a path prefix to apply to matching controller methods.
	 * Prefixes are used to enrich the mappings of every {@code @RequestMapping}
	 * method whose controller type is matched by the corresponding
	 * {@code Predicate}. The prefix for the first matching predicate is used.
	 * Consider using {@link org.springframework.web.method.HandlerTypePredicate
	 * HandlerTypePredicate} to group controllers.
	 * @param prefix the path prefix to apply
	 * @param predicate a predicate for matching controller types
	 * @since 5.1
	 */
	public PathMatchConfigurer addPathPrefix(String prefix, Predicate<Class<?>> predicate) {
		if (this.pathPrefixes == null) {
			this.pathPrefixes = new LinkedHashMap<>();
		}
		this.pathPrefixes.put(prefix, predicate);
		return this;
	}


	@Nullable
	protected Boolean isUseTrailingSlashMatch() {
		return this.trailingSlashMatch;
	}

	@Nullable
	protected Boolean isUseCaseSensitiveMatch() {
		return this.caseSensitiveMatch;
	}

	@Nullable
	protected Map<String, Predicate<Class<?>>> getPathPrefixes() {
		return this.pathPrefixes;
	}
}
