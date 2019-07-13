

package org.springframework.jdbc.core.namedparam;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcOperations;

/**
 * Generic utility methods for working with JDBC batch statements using named parameters.
 * Mainly for internal use within the framework.
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @since 3.0
 * @deprecated as of 5.1.3, not used by {@link NamedParameterJdbcTemplate} anymore
 */
@Deprecated
public abstract class NamedParameterBatchUpdateUtils extends org.springframework.jdbc.core.BatchUpdateUtils {

	public static int[] executeBatchUpdateWithNamedParameters(
			final ParsedSql parsedSql, final SqlParameterSource[] batchArgs, JdbcOperations jdbcOperations) {

		if (batchArgs.length == 0) {
			return new int[0];
		}

		String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, batchArgs[0]);
		return jdbcOperations.batchUpdate(
				sqlToUse,
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						Object[] values = NamedParameterUtils.buildValueArray(parsedSql, batchArgs[i], null);
						int[] columnTypes = NamedParameterUtils.buildSqlTypeArray(parsedSql, batchArgs[i]);
						setStatementParameters(values, ps, columnTypes);
					}
					@Override
					public int getBatchSize() {
						return batchArgs.length;
					}
				});
	}

}
