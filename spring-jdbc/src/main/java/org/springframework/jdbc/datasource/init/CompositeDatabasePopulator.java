

package org.springframework.jdbc.datasource.init;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Composite {@link DatabasePopulator} that delegates to a list of given
 * {@code DatabasePopulator} implementations, executing all scripts.
 *
 * @author Dave Syer
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Kazuki Shimizu
 * @since 3.1
 */
public class CompositeDatabasePopulator implements DatabasePopulator {

	private final List<DatabasePopulator> populators = new ArrayList<>(4);


	/**
	 * Create an empty {@code CompositeDatabasePopulator}.
	 * @see #setPopulators
	 * @see #addPopulators
	 */
	public CompositeDatabasePopulator() {
	}

	/**
	 * Create a {@code CompositeDatabasePopulator} with the given populators.
	 * @param populators one or more populators to delegate to
	 * @since 4.3
	 */
	public CompositeDatabasePopulator(Collection<DatabasePopulator> populators) {
		this.populators.addAll(populators);
	}

	/**
	 * Create a {@code CompositeDatabasePopulator} with the given populators.
	 * @param populators one or more populators to delegate to
	 * @since 4.3
	 */
	public CompositeDatabasePopulator(DatabasePopulator... populators) {
		this.populators.addAll(Arrays.asList(populators));
	}


	/**
	 * Specify one or more populators to delegate to.
	 */
	public void setPopulators(DatabasePopulator... populators) {
		this.populators.clear();
		this.populators.addAll(Arrays.asList(populators));
	}

	/**
	 * Add one or more populators to the list of delegates.
	 */
	public void addPopulators(DatabasePopulator... populators) {
		this.populators.addAll(Arrays.asList(populators));
	}


	@Override
	public void populate(Connection connection) throws SQLException, ScriptException {
		for (DatabasePopulator populator : this.populators) {
			populator.populate(connection);
		}
	}

}
