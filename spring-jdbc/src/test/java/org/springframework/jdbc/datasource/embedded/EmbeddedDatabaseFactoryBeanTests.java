

package org.springframework.jdbc.datasource.embedded;

import javax.sql.DataSource;

import org.junit.Test;

import org.springframework.core.io.ClassRelativeResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import static org.junit.Assert.*;


public class EmbeddedDatabaseFactoryBeanTests {

	private final ClassRelativeResourceLoader resourceLoader = new ClassRelativeResourceLoader(getClass());


	Resource resource(String path) {
		return resourceLoader.getResource(path);
	}

	@Test
	public void testFactoryBeanLifecycle()  {
		EmbeddedDatabaseFactoryBean bean = new EmbeddedDatabaseFactoryBean();
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource("db-schema.sql"),
			resource("db-test-data.sql"));
		bean.setDatabasePopulator(populator);
		bean.afterPropertiesSet();
		DataSource ds = bean.getObject();
		JdbcTemplate template = new JdbcTemplate(ds);
		assertEquals("Keith", template.queryForObject("select NAME from T_TEST", String.class));
		bean.destroy();
	}

}
