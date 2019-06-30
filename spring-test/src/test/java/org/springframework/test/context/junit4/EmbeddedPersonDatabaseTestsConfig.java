

package org.springframework.test.context.junit4;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Shared configuration for tests that need an embedded database pre-loaded
 * with the schema for the 'person' table.
 *
 * @author Sam Brannen
 * @since 4.2
 */
@Configuration
public class EmbeddedPersonDatabaseTestsConfig {

	@Bean
	public PlatformTransactionManager txMgr() {
		return new DataSourceTransactionManager(dataSource());
	}

	@Bean
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder()//
		.generateUniqueName(true)//
		.addScript("classpath:/org/springframework/test/jdbc/schema.sql") //
		.build();
	}

}
