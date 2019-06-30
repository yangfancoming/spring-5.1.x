

package org.springframework.test.context.configuration.interfaces;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.tests.sample.beans.Employee;

import static org.junit.Assert.*;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@RunWith(SpringRunner.class)
public class ContextConfigurationInterfaceTests implements ContextConfigurationTestInterface {

	@Autowired
	Employee employee;


	@Test
	public void profileFromTestInterface() {
		assertNotNull(employee);
		assertEquals("Dilbert", employee.getName());
	}

}
