

package org.springframework.jdbc.core.namedparam;

import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Abstract base class for {@link SqlParameterSource} implementations.
 * Provides registration of SQL types per parameter.
 *

 * @since 2.0
 */
public abstract class AbstractSqlParameterSource implements SqlParameterSource {

	private final Map<String, Integer> sqlTypes = new HashMap<>();

	private final Map<String, String> typeNames = new HashMap<>();


	/**
	 * Register a SQL type for the given parameter.
	 * @param paramName the name of the parameter
	 * @param sqlType the SQL type of the parameter
	 */
	public void registerSqlType(String paramName, int sqlType) {
		Assert.notNull(paramName, "Parameter name must not be null");
		this.sqlTypes.put(paramName, sqlType);
	}

	/**
	 * Register a SQL type for the given parameter.
	 * @param paramName the name of the parameter
	 * @param typeName the type name of the parameter
	 */
	public void registerTypeName(String paramName, String typeName) {
		Assert.notNull(paramName, "Parameter name must not be null");
		this.typeNames.put(paramName, typeName);
	}

	/**
	 * Return the SQL type for the given parameter, if registered.
	 * @param paramName the name of the parameter
	 * @return the SQL type of the parameter,
	 * or {@code TYPE_UNKNOWN} if not registered
	 */
	@Override
	public int getSqlType(String paramName) {
		Assert.notNull(paramName, "Parameter name must not be null");
		return this.sqlTypes.getOrDefault(paramName, TYPE_UNKNOWN);
	}

	/**
	 * Return the type name for the given parameter, if registered.
	 * @param paramName the name of the parameter
	 * @return the type name of the parameter,
	 * or {@code null} if not registered
	 */
	@Override
	@Nullable
	public String getTypeName(String paramName) {
		Assert.notNull(paramName, "Parameter name must not be null");
		return this.typeNames.get(paramName);
	}

}
