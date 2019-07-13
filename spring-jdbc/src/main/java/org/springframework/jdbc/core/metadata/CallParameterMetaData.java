

package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;

import org.springframework.lang.Nullable;

/**
 * Holder of meta-data for a specific parameter that is used for call processing.
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @since 2.5
 * @see GenericCallMetaDataProvider
 */
public class CallParameterMetaData {

	@Nullable
	private String parameterName;

	private int parameterType;

	private int sqlType;

	@Nullable
	private String typeName;

	private boolean nullable;


	/**
	 * Constructor taking all the properties.
	 */
	public CallParameterMetaData(
			@Nullable String columnName, int columnType, int sqlType, @Nullable String typeName, boolean nullable) {

		this.parameterName = columnName;
		this.parameterType = columnType;
		this.sqlType = sqlType;
		this.typeName = typeName;
		this.nullable = nullable;
	}


	/**
	 * Get the parameter name.
	 */
	@Nullable
	public String getParameterName() {
		return this.parameterName;
	}

	/**
	 * Get the parameter type.
	 */
	public int getParameterType() {
		return this.parameterType;
	}

	/**
	 * Determine whether the declared parameter qualifies as a 'return' parameter
	 * for our purposes: type {@link DatabaseMetaData#procedureColumnReturn} or
	 * {@link DatabaseMetaData#procedureColumnResult}.
	 * @since 4.3.15
	 */
	public boolean isReturnParameter() {
		return (this.parameterType == DatabaseMetaData.procedureColumnReturn ||
				this.parameterType == DatabaseMetaData.procedureColumnResult);
	}

	/**
	 * Get the parameter SQL type.
	 */
	public int getSqlType() {
		return this.sqlType;
	}

	/**
	 * Get the parameter type name.
	 */
	@Nullable
	public String getTypeName() {
		return this.typeName;
	}

	/**
	 * Get whether the parameter is nullable.
	 */
	public boolean isNullable() {
		return this.nullable;
	}

}
