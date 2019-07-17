

package org.springframework.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**

 * @since 28.05.2004
 */
public class UserCredentialsDataSourceAdapterTests {

	@Test
	public void testStaticCredentials() throws SQLException {
		DataSource dataSource = mock(DataSource.class);
		Connection connection = mock(Connection.class);
		given(dataSource.getConnection("user", "pw")).willReturn(connection);

		UserCredentialsDataSourceAdapter adapter = new UserCredentialsDataSourceAdapter();
		adapter.setTargetDataSource(dataSource);
		adapter.setUsername("user");
		adapter.setPassword("pw");
		assertEquals(connection, adapter.getConnection());
	}

	@Test
	public void testNoCredentials() throws SQLException {
		DataSource dataSource = mock(DataSource.class);
		Connection connection = mock(Connection.class);
		given(dataSource.getConnection()).willReturn(connection);
		UserCredentialsDataSourceAdapter adapter = new UserCredentialsDataSourceAdapter();
		adapter.setTargetDataSource(dataSource);
		assertEquals(connection, adapter.getConnection());
	}

	@Test
	public void testThreadBoundCredentials() throws SQLException {
		DataSource dataSource = mock(DataSource.class);
		Connection connection = mock(Connection.class);
		given(dataSource.getConnection("user", "pw")).willReturn(connection);

		UserCredentialsDataSourceAdapter adapter = new UserCredentialsDataSourceAdapter();
		adapter.setTargetDataSource(dataSource);

		adapter.setCredentialsForCurrentThread("user", "pw");
		try {
			assertEquals(connection, adapter.getConnection());
		}
		finally {
			adapter.removeCredentialsFromCurrentThread();
		}
	}

}
