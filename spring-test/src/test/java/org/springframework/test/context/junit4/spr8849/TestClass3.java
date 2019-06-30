

package org.springframework.test.context.junit4.spr8849;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * This name of this class intentionally does not end with "Test" or "Tests"
 * since it should only be run as part of the test suite: {@link Spr8849Tests}.
 *
 * @author Sam Brannen
 * @since 4.2
 * @see Spr8849Tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestClass3 {

	@Configuration
	@ImportResource("classpath:/org/springframework/test/context/junit4/spr8849/datasource-config-with-auto-generated-db-name.xml")
	static class Config {
	}


	@Resource
	DataSource dataSource;


	@Test
	public void dummyTest() {
		assertNotNull(dataSource);
	}

}
