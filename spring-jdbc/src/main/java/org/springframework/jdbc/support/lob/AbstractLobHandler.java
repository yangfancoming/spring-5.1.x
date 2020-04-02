

package org.springframework.jdbc.support.lob;

import java.io.InputStream;
import java.io.Reader;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.lang.Nullable;

/**
 * Abstract base class for {@link LobHandler} implementations.
 *
 * Implements all accessor methods for column names through a column lookup
 * and delegating to the corresponding accessor that takes a column index.
 *

 * @since 1.2
 * @see java.sql.ResultSet#findColumn
 */
public abstract class AbstractLobHandler implements LobHandler {

	@Override
	@Nullable
	public byte[] getBlobAsBytes(ResultSet rs, String columnName) throws SQLException {
		return getBlobAsBytes(rs, rs.findColumn(columnName));
	}

	@Override
	@Nullable
	public InputStream getBlobAsBinaryStream(ResultSet rs, String columnName) throws SQLException {
		return getBlobAsBinaryStream(rs, rs.findColumn(columnName));
	}

	@Override
	@Nullable
	public String getClobAsString(ResultSet rs, String columnName) throws SQLException {
		return getClobAsString(rs, rs.findColumn(columnName));
	}

	@Override
	@Nullable
	public InputStream getClobAsAsciiStream(ResultSet rs, String columnName) throws SQLException {
		return getClobAsAsciiStream(rs, rs.findColumn(columnName));
	}

	@Override
	public Reader getClobAsCharacterStream(ResultSet rs, String columnName) throws SQLException {
		return getClobAsCharacterStream(rs, rs.findColumn(columnName));
	}

}
