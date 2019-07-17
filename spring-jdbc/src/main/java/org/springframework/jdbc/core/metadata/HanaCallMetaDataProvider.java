

package org.springframework.jdbc.core.metadata;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * SAP HANA specific implementation for the {@link CallMetaDataProvider} interface.
 * This class is intended for internal use by the Simple JDBC classes.
 *
 * @author Subhobrata Dey

 * @since 4.2.1
 */
public class HanaCallMetaDataProvider extends GenericCallMetaDataProvider {

	public HanaCallMetaDataProvider(DatabaseMetaData databaseMetaData) throws SQLException {
		super(databaseMetaData);
	}


	@Override
	public void initializeWithMetaData(DatabaseMetaData databaseMetaData) throws SQLException {
		super.initializeWithMetaData(databaseMetaData);
		setStoresUpperCaseIdentifiers(false);
	}

}
