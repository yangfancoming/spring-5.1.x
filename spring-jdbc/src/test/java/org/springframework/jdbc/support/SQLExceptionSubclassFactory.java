

package org.springframework.jdbc.support;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLInvalidAuthorizationSpecException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLRecoverableException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransactionRollbackException;
import java.sql.SQLTransientConnectionException;

/**
 * Class to generate Java 6 SQLException subclasses for testing purposes.
 *
 * @author Thomas Risberg
 */
public class SQLExceptionSubclassFactory {

	public static SQLException newSQLDataException(String reason, String SQLState, int vendorCode) {
		return new SQLDataException(reason, SQLState, vendorCode);
	}

	public static SQLException newSQLFeatureNotSupportedException(String reason, String SQLState, int vendorCode) {
		return new SQLFeatureNotSupportedException(reason, SQLState, vendorCode);
	}

	public static SQLException newSQLIntegrityConstraintViolationException(String reason, String SQLState, int vendorCode) {
		return new SQLIntegrityConstraintViolationException(reason, SQLState, vendorCode);
	}

	public static SQLException newSQLInvalidAuthorizationSpecException(String reason, String SQLState, int vendorCode) {
		return new SQLInvalidAuthorizationSpecException(reason, SQLState, vendorCode);
	}

	public static SQLException newSQLNonTransientConnectionException(String reason, String SQLState, int vendorCode) {
		return new SQLNonTransientConnectionException(reason, SQLState, vendorCode);
	}

	public static SQLException newSQLSyntaxErrorException(String reason, String SQLState, int vendorCode) {
		return new SQLSyntaxErrorException(reason, SQLState, vendorCode);
	}

	public static SQLException newSQLTransactionRollbackException(String reason, String SQLState, int vendorCode) {
		return new SQLTransactionRollbackException(reason, SQLState, vendorCode);
	}

	public static SQLException newSQLTransientConnectionException(String reason, String SQLState, int vendorCode) {
		return new SQLTransientConnectionException(reason, SQLState, vendorCode);
	}

	public static SQLException newSQLTimeoutException(String reason, String SQLState, int vendorCode) {
		return new SQLTimeoutException(reason, SQLState, vendorCode);
	}

	public static SQLException newSQLRecoverableException(String reason, String SQLState, int vendorCode) {
		return new SQLRecoverableException(reason, SQLState, vendorCode);
	}

}
