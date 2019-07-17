

package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.springframework.lang.Nullable;

/**
 * Derby specific implementation for the {@link CallMetaDataProvider} interface.
 * This class is intended for internal use by the Simple JDBC classes.
 *
 * @author Thomas Risberg

 * @since 2.5
 */
public class DerbyCallMetaDataProvider extends GenericCallMetaDataProvider {

	public DerbyCallMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
		super(databaseMetaData);
	}


	@Override
	@Nullable
	public String metaDataSchemaNameToUse(@Nullable String schemaName) {
		if (schemaName != null) {
			return super.metaDataSchemaNameToUse(schemaName);
		}

		// Use current user schema if no schema specified...
		String userName = getUserName();
		return (userName != null ? userName.toUpperCase() : null);
	}

}
