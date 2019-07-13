

package org.springframework.jdbc.support;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.lang.Nullable;

/**
 * Custom SQLException translation for testing.
 *
 * @author Thomas Risberg
 */
public class CustomSqlExceptionTranslator implements SQLExceptionTranslator {

	@Override
	public DataAccessException translate(String task, @Nullable String sql, SQLException ex) {
		if (ex.getErrorCode() == 2) {
			return new TransientDataAccessResourceException("Custom", ex);
		}
		return null;
	}

}
