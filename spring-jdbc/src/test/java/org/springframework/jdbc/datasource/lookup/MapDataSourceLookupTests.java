

package org.springframework.jdbc.datasource.lookup;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;


public class MapDataSourceLookupTests {

	private static final String DATA_SOURCE_NAME = "dataSource";

	@Rule
	public final ExpectedException exception = ExpectedException.none();


	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getDataSourcesReturnsUnmodifiableMap() throws Exception {
		MapDataSourceLookup lookup = new MapDataSourceLookup();
		Map dataSources = lookup.getDataSources();

		exception.expect(UnsupportedOperationException.class);
		dataSources.put("", "");
	}

	@Test
	public void lookupSunnyDay() throws Exception {
		Map<String, DataSource> dataSources = new HashMap<>();
		StubDataSource expectedDataSource = new StubDataSource();
		dataSources.put(DATA_SOURCE_NAME, expectedDataSource);
		MapDataSourceLookup lookup = new MapDataSourceLookup();
		lookup.setDataSources(dataSources);
		DataSource dataSource = lookup.getDataSource(DATA_SOURCE_NAME);
		assertNotNull("A DataSourceLookup implementation must *never* return null from getDataSource(): this one obviously (and incorrectly) is", dataSource);
		assertSame(expectedDataSource, dataSource);
	}

	@Test
	public void setDataSourcesIsAnIdempotentOperation() throws Exception {
		Map<String, DataSource> dataSources = new HashMap<>();
		StubDataSource expectedDataSource = new StubDataSource();
		dataSources.put(DATA_SOURCE_NAME, expectedDataSource);
		MapDataSourceLookup lookup = new MapDataSourceLookup();
		lookup.setDataSources(dataSources);
		lookup.setDataSources(null); // must be idempotent (i.e. the following lookup must still work);
		DataSource dataSource = lookup.getDataSource(DATA_SOURCE_NAME);
		assertNotNull("A DataSourceLookup implementation must *never* return null from getDataSource(): this one obviously (and incorrectly) is", dataSource);
		assertSame(expectedDataSource, dataSource);
	}

	@Test
	public void addingDataSourcePermitsOverride() throws Exception {
		Map<String, DataSource> dataSources = new HashMap<>();
		StubDataSource overridenDataSource = new StubDataSource();
		StubDataSource expectedDataSource = new StubDataSource();
		dataSources.put(DATA_SOURCE_NAME, overridenDataSource);
		MapDataSourceLookup lookup = new MapDataSourceLookup();
		lookup.setDataSources(dataSources);
		lookup.addDataSource(DATA_SOURCE_NAME, expectedDataSource); // must override existing entry
		DataSource dataSource = lookup.getDataSource(DATA_SOURCE_NAME);
		assertNotNull("A DataSourceLookup implementation must *never* return null from getDataSource(): this one obviously (and incorrectly) is", dataSource);
		assertSame(expectedDataSource, dataSource);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getDataSourceWhereSuppliedMapHasNonDataSourceTypeUnderSpecifiedKey() throws Exception {
		Map dataSources = new HashMap();
		dataSources.put(DATA_SOURCE_NAME, new Object());
		MapDataSourceLookup lookup = new MapDataSourceLookup(dataSources);

		exception.expect(ClassCastException.class);
		lookup.getDataSource(DATA_SOURCE_NAME);
	}

	@Test
	public void getDataSourceWhereSuppliedMapHasNoEntryForSpecifiedKey() throws Exception {
		MapDataSourceLookup lookup = new MapDataSourceLookup();

		exception.expect(DataSourceLookupFailureException.class);
		lookup.getDataSource(DATA_SOURCE_NAME);
	}

}
