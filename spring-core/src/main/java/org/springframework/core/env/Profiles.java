

package org.springframework.core.env;

import java.util.function.Predicate;

/**
 * Profile predicate that may be {@linkplain Environment#acceptsProfiles(Profiles) accepted} by an {@link Environment}.
 * May be implemented directly or, more usually, created using the {@link #of(String...) of(...)} factory method.
 * @since 5.1
 */
@FunctionalInterface
public interface Profiles {

	/**
	 * Test if this {@code Profiles} instance <em>matches</em> against the given active profiles predicate.
	 * @param activeProfiles predicate that tests whether a given profile is currently active
	 */
	boolean matches(Predicate<String> activeProfiles);


	/**
	 * Create a new {@link Profiles} instance that checks for matches against the given <em>profile strings</em>.
	 * The returned instance will {@linkplain Profiles#matches(Predicate) match} if any one of the given profile strings matches.
	 * A profile string may contain a simple profile name (for example {@code "production"}) or a profile expression.
	 * A profile expression allows  for more complicated profile logic to be expressed, for example {@code "production & cloud"}.
	 * The following operators are supported in profile expressions:
	 * <li>{@code !} - A logical <em>not</em> of the profile</li>
	 * <li>{@code &} - A logical <em>and</em> of the profiles</li>
	 * <li>{@code |} - A logical <em>or</em> of the profiles</li>
	 * Please note that the {@code &} and {@code |} operators may not be mixed without using parentheses.
	 * For example {@code "a & b | c"} is not a valid expression; it must be expressed as {@code "(a & b) | c"} or {@code "a & (b | c)"}.
	 * @param profiles the <em>profile strings</em> to include
	 * @return a new {@link Profiles} instance
	 */
	static Profiles of(String... profiles) {
		return ProfilesParser.parse(profiles);
	}

}
