

package org.springframework.jdbc.core.support;

import java.util.Properties;
import javax.sql.DataSource;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Bean definition reader that reads values from a database table,
 * based on a given SQL statement.
 *
 * <p>Expects columns for bean name, property name and value as String.
 * Formats for each are identical to the properties format recognized
 * by PropertiesBeanDefinitionReader.
 *
 * <p><b>NOTE:</b> This is mainly intended as an example for a custom
 * JDBC-based bean definition reader. It does not aim to offer
 * comprehensive functionality.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #loadBeanDefinitions
 * @see org.springframework.beans.factory.support.PropertiesBeanDefinitionReader
 */
public class JdbcBeanDefinitionReader {

	private final PropertiesBeanDefinitionReader propReader;

	@Nullable
	private JdbcTemplate jdbcTemplate;


	/**
	 * Create a new JdbcBeanDefinitionReader for the given bean factory,
	 * using a default PropertiesBeanDefinitionReader underneath.
	 * <p>DataSource or JdbcTemplate still need to be set.
	 * @see #setDataSource
	 * @see #setJdbcTemplate
	 */
	public JdbcBeanDefinitionReader(BeanDefinitionRegistry beanFactory) {
		this.propReader = new PropertiesBeanDefinitionReader(beanFactory);
	}

	/**
	 * Create a new JdbcBeanDefinitionReader that delegates to the
	 * given PropertiesBeanDefinitionReader underneath.
	 * <p>DataSource or JdbcTemplate still need to be set.
	 * @see #setDataSource
	 * @see #setJdbcTemplate
	 */
	public JdbcBeanDefinitionReader(PropertiesBeanDefinitionReader beanDefinitionReader) {
		Assert.notNull(beanDefinitionReader, "Bean definition reader must not be null");
		this.propReader = beanDefinitionReader;
	}


	/**
	 * Set the DataSource to use to obtain database connections.
	 * Will implicitly create a new JdbcTemplate with the given DataSource.
	 */
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * Set the JdbcTemplate to be used by this bean factory.
	 * Contains settings for DataSource, SQLExceptionTranslator, etc.
	 */
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		Assert.notNull(jdbcTemplate, "JdbcTemplate must not be null");
		this.jdbcTemplate = jdbcTemplate;
	}


	/**
	 * Load bean definitions from the database via the given SQL string.
	 * @param sql the SQL query to use for loading bean definitions.
	 * The first three columns must be bean name, property name and value.
	 * Any join and any other columns are permitted: e.g.
	 * {@code SELECT BEAN_NAME, PROPERTY, VALUE FROM CONFIG WHERE CONFIG.APP_ID = 1}
	 * It's also possible to perform a join. Column names are not significant --
	 * only the ordering of these first three columns.
	 */
	public void loadBeanDefinitions(String sql) {
		Assert.notNull(this.jdbcTemplate, "Not fully configured - specify DataSource or JdbcTemplate");
		final Properties props = new Properties();
		this.jdbcTemplate.query(sql, rs -> {
			String beanName = rs.getString(1);
			String property = rs.getString(2);
			String value = rs.getString(3);
			// Make a properties entry by combining bean name and property.
			props.setProperty(beanName + '.' + property, value);
		});
		this.propReader.registerBeanDefinitions(props);
	}

}
