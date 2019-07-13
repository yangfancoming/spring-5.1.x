

package org.springframework.jdbc.core.namedparam;

import org.springframework.lang.Nullable;

/**
 * A simple empty implementation of the {@link SqlParameterSource} interface.
 *
 * @author Juergen Hoeller
 * @since 3.2.2
 */
public class EmptySqlParameterSource implements SqlParameterSource {

	/**
	 * A shared instance of {@link EmptySqlParameterSource}.
	 */
	public static final EmptySqlParameterSource INSTANCE = new EmptySqlParameterSource();


	@Override
	public boolean hasValue(String paramName) {
		return false;
	}

	@Override
	@Nullable
	public Object getValue(String paramName) throws IllegalArgumentException {
		throw new IllegalArgumentException("This SqlParameterSource is empty");
	}

	@Override
	public int getSqlType(String paramName) {
		return TYPE_UNKNOWN;
	}

	@Override
	@Nullable
	public String getTypeName(String paramName) {
		return null;
	}

	@Override
	@Nullable
	public String[] getParameterNames() {
		return null;
	}

}
