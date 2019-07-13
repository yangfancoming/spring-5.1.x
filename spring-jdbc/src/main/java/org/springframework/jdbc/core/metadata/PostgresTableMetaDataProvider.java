

package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * The PostgreSQL specific implementation of {@link TableMetaDataProvider}.
 * Supports a feature for retrieving generated keys without the JDBC 3.0
 * {@code getGeneratedKeys} support.
 *
 * @author Thomas Risberg
 * @since 2.5
 */
public class PostgresTableMetaDataProvider extends GenericTableMetaDataProvider {

	public PostgresTableMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
		super(databaseMetaData);
	}


	@Override
	public boolean isGetGeneratedKeysSimulated() {
		return true;
	}

	@Override
	public String getSimpleQueryForGetGeneratedKey(String tableName, String keyColumnName) {
		return "RETURNING " + keyColumnName;
	}

}
