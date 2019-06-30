

package org.springframework.test.context.jdbc;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.transaction.TransactionTestUtils;

import static org.junit.Assert.*;

/**
 * Integration tests that ensure that <em>primary</em> data sources are
 * supported.
 *
 * @author Sam Brannen
 * @since 4.3
 * @see org.springframework.test.context.transaction.PrimaryTransactionManagerTests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
public class PrimaryDataSourceTests {

	@Configuration
	static class Config {

		@Primary
		@Bean
		public DataSource primaryDataSource() {
			// @formatter:off
			return new EmbeddedDatabaseBuilder()
					.generateUniqueName(true)
					.addScript("classpath:/org/springframework/test/context/jdbc/schema.sql")
					.build();
			// @formatter:on
		}

		@Bean
		public DataSource additionalDataSource() {
			return new EmbeddedDatabaseBuilder().generateUniqueName(true).build();
		}

	}


	private JdbcTemplate jdbcTemplate;


	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Test
	@Sql("data.sql")
	public void dataSourceTest() {
		TransactionTestUtils.assertInTransaction(false);
		assertEquals("Number of rows in the 'user' table.", 1,
			JdbcTestUtils.countRowsInTable(this.jdbcTemplate, "user"));
	}

}
