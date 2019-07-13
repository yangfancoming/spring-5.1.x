

package org.springframework.jdbc.datasource.init;

import org.junit.Test;
import org.mockito.Mockito;

import org.springframework.core.io.Resource;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ResourceDatabasePopulator}.
 *
 * @author Sam Brannen
 * @since 4.1
 * @see AbstractDatabasePopulatorTests
 */
public class ResourceDatabasePopulatorTests {

	private static final Resource script1 = Mockito.mock(Resource.class);
	private static final Resource script2 = Mockito.mock(Resource.class);
	private static final Resource script3 = Mockito.mock(Resource.class);


	@Test(expected = IllegalArgumentException.class)
	public void constructWithNullResource() {
		new ResourceDatabasePopulator((Resource) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructWithNullResourceArray() {
		new ResourceDatabasePopulator((Resource[]) null);
	}

	@Test
	public void constructWithResource() {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(script1);
		assertEquals(1, databasePopulator.scripts.size());
	}

	@Test
	public void constructWithMultipleResources() {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(script1, script2);
		assertEquals(2, databasePopulator.scripts.size());
	}

	@Test
	public void constructWithMultipleResourcesAndThenAddScript() {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator(script1, script2);
		assertEquals(2, databasePopulator.scripts.size());

		databasePopulator.addScript(script3);
		assertEquals(3, databasePopulator.scripts.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void addScriptsWithNullResource() {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScripts((Resource) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addScriptsWithNullResourceArray() {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.addScripts((Resource[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setScriptsWithNullResource() {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.setScripts((Resource) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setScriptsWithNullResourceArray() {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		databasePopulator.setScripts((Resource[]) null);
	}

	@Test
	public void setScriptsAndThenAddScript() {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		assertEquals(0, databasePopulator.scripts.size());

		databasePopulator.setScripts(script1, script2);
		assertEquals(2, databasePopulator.scripts.size());

		databasePopulator.addScript(script3);
		assertEquals(3, databasePopulator.scripts.size());
	}

}
