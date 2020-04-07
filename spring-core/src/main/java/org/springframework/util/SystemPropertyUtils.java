

package org.springframework.util;

import org.springframework.lang.Nullable;

/**
 * Helper class for resolving placeholders in texts. Usually applied to file paths.
 *
 * A text may contain {@code ${...}} placeholders, to be resolved as system properties:
 * e.g. {@code ${user.dir}}. Default values can be supplied using the ":" separator between key and value.
 * @since 1.2.5
 * @see #PLACEHOLDER_PREFIX
 * @see #PLACEHOLDER_SUFFIX
 * @see System#getProperty(String)
 */
public abstract class SystemPropertyUtils {

	/** Prefix for system property placeholders: "${". */
	public static final String PLACEHOLDER_PREFIX = "${";

	/** Suffix for system property placeholders: "}". */
	public static final String PLACEHOLDER_SUFFIX = "}";

	/** Value separator for system property placeholders: ":". */
	public static final String VALUE_SEPARATOR = ":";

	private static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, false);
	private static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, true);


	/**
	 * Resolve {@code ${...}} placeholders in the given text, replacing them with
	 * corresponding system property values.
	 * @param text the String to resolve
	 * @return the resolved String
	 * @throws IllegalArgumentException if there is an unresolvable placeholder
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 */
	public static String resolvePlaceholders(String text) {
		return resolvePlaceholders(text, false);
	}

	/**
	 * Resolve {@code ${...}} placeholders in the given text, replacing them with
	 * corresponding system property values. Unresolvable placeholders with no default
	 * value are ignored and passed through unchanged if the flag is set to {@code true}.
	 * @param text the String to resolve
	 * @param ignoreUnresolvablePlaceholders whether unresolved placeholders are to be ignored
	 * @return the resolved String
	 * @throws IllegalArgumentException if there is an unresolvable placeholder
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 * and the "ignoreUnresolvablePlaceholders" flag is {@code false}
	 */
	public static String resolvePlaceholders(String text, boolean ignoreUnresolvablePlaceholders) {
		PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper);
		return helper.replacePlaceholders(text, new SystemPropertyPlaceholderResolver(text));
	}


	/**
	 * PlaceholderResolver implementation that resolves against system properties
	 * and system environment variables.
	 */
	private static class SystemPropertyPlaceholderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

		private final String text;

		public SystemPropertyPlaceholderResolver(String text) {
			this.text = text;
		}

		@Override
		@Nullable
		public String resolvePlaceholder(String placeholderName) {
			try {
				String propVal = System.getProperty(placeholderName);
				if (propVal == null) {
					// Fall back to searching the system environment.
					propVal = System.getenv(placeholderName);
				}
				return propVal;
			}catch (Throwable ex) {
				System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" + this.text + "] as system property: " + ex);
				return null;
			}
		}
	}

}
