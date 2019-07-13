

package org.springframework.jdbc.core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.lang.Nullable;

/**
 * Generic utility methods for working with JDBC batch statements.
 * Mainly for internal use within the framework.
 *
 * @author Thomas Risberg
 * @author Juergen Hoeller
 * @since 3.0
 * @deprecated as of 5.1.3, not used by {@link JdbcTemplate} anymore
 */
@Deprecated
public abstract class BatchUpdateUtils {

	public static int[] executeBatchUpdate(
			String sql, final List<Object[]> batchArgs, final int[] columnTypes, JdbcOperations jdbcOperations) {

		if (batchArgs.isEmpty()) {
			return new int[0];
		}

		return jdbcOperations.batchUpdate(
				sql,
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						Object[] values = batchArgs.get(i);
						setStatementParameters(values, ps, columnTypes);
					}
					@Override
					public int getBatchSize() {
						return batchArgs.size();
					}
				});
	}

	protected static void setStatementParameters(Object[] values, PreparedStatement ps, @Nullable int[] columnTypes)
			throws SQLException {

		int colIndex = 0;
		for (Object value : values) {
			colIndex++;
			if (value instanceof SqlParameterValue) {
				SqlParameterValue paramValue = (SqlParameterValue) value;
				StatementCreatorUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
			}
			else {
				int colType;
				if (columnTypes == null || columnTypes.length < colIndex) {
					colType = SqlTypeValue.TYPE_UNKNOWN;
				}
				else {
					colType = columnTypes[colIndex - 1];
				}
				StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
			}
		}
	}

}
