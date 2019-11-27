

package org.springframework.jdbc.datasource.lookup;

import javax.sql.DataSource;

import org.junit.Test;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;


public class BeanFactoryDataSourceLookupTests {

	private static final String DATASOURCE_BEAN_NAME = "dataSource";


	@Test
	public void testLookupSunnyDay() {
		BeanFactory beanFactory = mock(BeanFactory.class);

		StubDataSource expectedDataSource = new StubDataSource();
		given(beanFactory.getBean(DATASOURCE_BEAN_NAME, DataSource.class)).willReturn(expectedDataSource);

		BeanFactoryDataSourceLookup lookup = new BeanFactoryDataSourceLookup();
		lookup.setBeanFactory(beanFactory);
		DataSource dataSource = lookup.getDataSource(DATASOURCE_BEAN_NAME);
		assertNotNull("A DataSourceLookup implementation must *never* return null from " +
				"getDataSource(): this one obviously (and incorrectly) is", dataSource);
		assertSame(expectedDataSource, dataSource);
	}

	@Test
	public void testLookupWhereBeanFactoryYieldsNonDataSourceType() throws Exception {
		final BeanFactory beanFactory = mock(BeanFactory.class);

		given(beanFactory.getBean(DATASOURCE_BEAN_NAME, DataSource.class)).willThrow(
				new BeanNotOfRequiredTypeException(DATASOURCE_BEAN_NAME,
						DataSource.class, String.class));

		try {
				BeanFactoryDataSourceLookup lookup = new BeanFactoryDataSourceLookup(beanFactory);
				lookup.getDataSource(DATASOURCE_BEAN_NAME);
				fail("should have thrown DataSourceLookupFailureException");
		}
		catch (DataSourceLookupFailureException ex) { /* expected */ }
	}

	@Test(expected = IllegalStateException.class)
	public void testLookupWhereBeanFactoryHasNotBeenSupplied() throws Exception {
		BeanFactoryDataSourceLookup lookup = new BeanFactoryDataSourceLookup();
		lookup.getDataSource(DATASOURCE_BEAN_NAME);
	}

}
