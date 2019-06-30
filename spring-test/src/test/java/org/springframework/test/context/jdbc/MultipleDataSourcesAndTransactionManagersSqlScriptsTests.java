

package org.springframework.test.context.jdbc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link Sql @Sql} that verify support for multiple
 * {@link DataSource}s and {@link PlatformTransactionManager}s.
 * <p>Simultaneously tests for method-level overrides via {@code @SqlConfig}.
 *
 * @author Sam Brannen
 * @since 4.1
 * @see MultipleDataSourcesAndTransactionManagersTransactionalSqlScriptsTests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
@SqlConfig(dataSource = "dataSource1", transactionManager = "txMgr1")
public class MultipleDataSourcesAndTransactionManagersSqlScriptsTests {

	@Autowired
	private DataSource dataSource1;

	@Autowired
	private DataSource dataSource2;


	@Test
	@Sql("data-add-dogbert.sql")
	public void database1() {
		assertUsers(new JdbcTemplate(dataSource1), "Dilbert", "Dogbert");
	}

	@Test
	@Sql(scripts = "data-add-catbert.sql", config = @SqlConfig(dataSource = "dataSource2", transactionManager = "txMgr2"))
	public void database2() {
		assertUsers(new JdbcTemplate(dataSource2), "Dilbert", "Catbert");
	}

	private void assertUsers(JdbcTemplate jdbcTemplate, String... users) {
		List<String> expected = Arrays.asList(users);
		Collections.sort(expected);
		List<String> actual = jdbcTemplate.queryForList("select name from user", String.class);
		Collections.sort(actual);
		assertEquals("Users in database;", expected, actual);
	}


	@Configuration
	static class Config {

		@Bean
		public PlatformTransactionManager txMgr1() {
			return new DataSourceTransactionManager(dataSource1());
		}

		@Bean
		public PlatformTransactionManager txMgr2() {
			return new DataSourceTransactionManager(dataSource2());
		}

		@Bean
		public DataSource dataSource1() {
			return new EmbeddedDatabaseBuilder()//
			.setName("database1")//
			.addScript("classpath:/org/springframework/test/context/jdbc/schema.sql")//
			.addScript("classpath:/org/springframework/test/context/jdbc/data.sql")//
			.build();
		}

		@Bean
		public DataSource dataSource2() {
			return new EmbeddedDatabaseBuilder()//
			.setName("database2")//
			.addScript("classpath:/org/springframework/test/context/jdbc/schema.sql")//
			.addScript("classpath:/org/springframework/test/context/jdbc/data.sql")//
			.build();
		}

	}

}
