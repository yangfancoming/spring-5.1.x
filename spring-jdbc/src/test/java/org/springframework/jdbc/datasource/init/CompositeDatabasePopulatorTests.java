

package org.springframework.jdbc.datasource.init;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CompositeDatabasePopulator}.
 *
 * @author Kazuki Shimizu

 * @since 4.3
 */
public class CompositeDatabasePopulatorTests {

	private final Connection mockedConnection = mock(Connection.class);

	private final DatabasePopulator mockedDatabasePopulator1 = mock(DatabasePopulator.class);

	private final DatabasePopulator mockedDatabasePopulator2 = mock(DatabasePopulator.class);


	@Test
	public void addPopulators() throws SQLException {
		CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
		populator.addPopulators(mockedDatabasePopulator1, mockedDatabasePopulator2);
		populator.populate(mockedConnection);
		verify(mockedDatabasePopulator1,times(1)).populate(mockedConnection);
		verify(mockedDatabasePopulator2, times(1)).populate(mockedConnection);
	}

	@Test
	public void setPopulatorsWithMultiple() throws SQLException {
		CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
		populator.setPopulators(mockedDatabasePopulator1, mockedDatabasePopulator2);  // multiple
		populator.populate(mockedConnection);
		verify(mockedDatabasePopulator1, times(1)).populate(mockedConnection);
		verify(mockedDatabasePopulator2, times(1)).populate(mockedConnection);
	}

	@Test
	public void setPopulatorsForOverride() throws SQLException {
		CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
		populator.setPopulators(mockedDatabasePopulator1);
		populator.setPopulators(mockedDatabasePopulator2);  // override
		populator.populate(mockedConnection);
		verify(mockedDatabasePopulator1, times(0)).populate(mockedConnection);
		verify(mockedDatabasePopulator2, times(1)).populate(mockedConnection);
	}

	@Test
	public void constructWithVarargs() throws SQLException {
		CompositeDatabasePopulator populator =
				new CompositeDatabasePopulator(mockedDatabasePopulator1, mockedDatabasePopulator2);
		populator.populate(mockedConnection);
		verify(mockedDatabasePopulator1, times(1)).populate(mockedConnection);
		verify(mockedDatabasePopulator2, times(1)).populate(mockedConnection);
	}

	@Test
	public void constructWithCollection() throws SQLException {
		Set<DatabasePopulator> populators = new LinkedHashSet<>();
		populators.add(mockedDatabasePopulator1);
		populators.add(mockedDatabasePopulator2);
		CompositeDatabasePopulator populator = new CompositeDatabasePopulator(populators);
		populator.populate(mockedConnection);
		verify(mockedDatabasePopulator1, times(1)).populate(mockedConnection);
		verify(mockedDatabasePopulator2, times(1)).populate(mockedConnection);
	}

}
