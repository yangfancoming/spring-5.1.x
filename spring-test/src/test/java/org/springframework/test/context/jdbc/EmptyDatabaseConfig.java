

package org.springframework.test.context.jdbc;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Empty database configuration class for SQL script integration tests.
 *
 * @author Sam Brannen
 * @since 4.1
 */
@Configuration
public class EmptyDatabaseConfig {

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()//
		.setName("empty-sql-scripts-test-db")//
		.build();
	}

}
