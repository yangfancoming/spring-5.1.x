

package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.lang.Nullable;

/**
 * Oracle-specific implementation for the {@link CallMetaDataProvider} interface.
 * This class is intended for internal use by the Simple JDBC classes.
 *
 * @author Thomas Risberg
 * @since 2.5
 */
public class OracleCallMetaDataProvider extends GenericCallMetaDataProvider {

	private static final String REF_CURSOR_NAME = "REF CURSOR";


	public OracleCallMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
		super(databaseMetaData);
	}


	@Override
	public boolean isReturnResultSetSupported() {
		return false;
	}

	@Override
	public boolean isRefCursorSupported() {
		return true;
	}

	@Override
	public int getRefCursorSqlType() {
		return -10;
	}

	@Override
	@Nullable
	public String metaDataCatalogNameToUse(@Nullable String catalogName) {
		// Oracle uses catalog name for package name or an empty string if no package
		return (catalogName == null ? "" : catalogNameToUse(catalogName));
	}

	@Override
	@Nullable
	public String metaDataSchemaNameToUse(@Nullable String schemaName) {
		// Use current user schema if no schema specified
		return (schemaName == null ? getUserName() : super.metaDataSchemaNameToUse(schemaName));
	}

	@Override
	public SqlParameter createDefaultOutParameter(String parameterName, CallParameterMetaData meta) {
		if (meta.getSqlType() == Types.OTHER && REF_CURSOR_NAME.equals(meta.getTypeName())) {
			return new SqlOutParameter(parameterName, getRefCursorSqlType(), new ColumnMapRowMapper());
		}
		else {
			return super.createDefaultOutParameter(parameterName, meta);
		}
	}

}
