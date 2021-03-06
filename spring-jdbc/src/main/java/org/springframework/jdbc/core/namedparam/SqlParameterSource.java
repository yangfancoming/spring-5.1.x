

package org.springframework.jdbc.core.namedparam;

import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;

/**
 * Interface that defines common functionality for objects that can
 * offer parameter values for named SQL parameters, serving as argument
 * for {@link NamedParameterJdbcTemplate} operations.
 *
 * This interface allows for the specification of SQL type in addition
 * to parameter values. All parameter values and types are identified by
 * specifying the name of the parameter.
 *
 * Intended to wrap various implementations like a Map or a JavaBean
 * with a consistent interface.
 *
 * @author Thomas Risberg

 * @since 2.0
 * @see NamedParameterJdbcOperations
 * @see NamedParameterJdbcTemplate
 * @see MapSqlParameterSource
 * @see BeanPropertySqlParameterSource
 */
public interface SqlParameterSource {

	/**
	 * Constant that indicates an unknown (or unspecified) SQL type.
	 * To be returned from {@code getType} when no specific SQL type known.
	 * @see #getSqlType
	 * @see java.sql.Types
	 */
	int TYPE_UNKNOWN = JdbcUtils.TYPE_UNKNOWN;


	/**
	 * Determine whether there is a value for the specified named parameter.
	 * @param paramName the name of the parameter
	 * @return whether there is a value defined
	 */
	boolean hasValue(String paramName);

	/**
	 * Return the parameter value for the requested named parameter.
	 * @param paramName the name of the parameter
	 * @return the value of the specified parameter
	 * @throws IllegalArgumentException if there is no value for the requested parameter
	 */
	@Nullable
	Object getValue(String paramName) throws IllegalArgumentException;

	/**
	 * Determine the SQL type for the specified named parameter.
	 * @param paramName the name of the parameter
	 * @return the SQL type of the specified parameter,
	 * or {@code TYPE_UNKNOWN} if not known
	 * @see #TYPE_UNKNOWN
	 */
	default int getSqlType(String paramName) {
		return TYPE_UNKNOWN;
	}

	/**
	 * Determine the type name for the specified named parameter.
	 * @param paramName the name of the parameter
	 * @return the type name of the specified parameter,
	 * or {@code null} if not known
	 */
	@Nullable
	default String getTypeName(String paramName) {
		return null;
	}

	/**
	 * Extract all available parameter names if possible.
	 * This is an optional operation, primarily for use with
	 * {@link org.springframework.jdbc.core.simple.SimpleJdbcInsert}
	 * and {@link org.springframework.jdbc.core.simple.SimpleJdbcCall}.
	 * @return the array of parameter names, or {@code null} if not determinable
	 * @since 5.0.3
	 * @see SqlParameterSourceUtils#extractCaseInsensitiveParameterNames
	 */
	@Nullable
	default String[] getParameterNames() {
		return null;
	}

}
