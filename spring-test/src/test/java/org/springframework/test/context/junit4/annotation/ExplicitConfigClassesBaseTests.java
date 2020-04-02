

package org.springframework.test.context.junit4.annotation;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.tests.sample.beans.Employee;

import static org.junit.Assert.*;

/**
 * Integration tests that verify support for configuration classes in
 * the Spring TestContext Framework.
 *
 * Configuration will be loaded from {@link DefaultConfigClassesBaseTests.ContextConfiguration}.
 *
 * @author Sam Brannen
 * @since 3.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = DefaultConfigClassesBaseTests.ContextConfiguration.class)
public class ExplicitConfigClassesBaseTests {

	@Autowired
	protected Employee employee;


	@Test
	public void verifyEmployeeSetFromBaseContextConfig() {
		assertNotNull("The employee should have been autowired.", this.employee);
		assertEquals("John Smith", this.employee.getName());
	}

}
