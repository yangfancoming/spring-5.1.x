

package org.springframework.orm.jpa.support;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryIntegrationTests;
import org.springframework.orm.jpa.support.PersistenceInjectionTests.DefaultPublicPersistenceContextSetter;
import org.springframework.orm.jpa.support.PersistenceInjectionTests.DefaultPublicPersistenceUnitSetterNamedPerson;

import static org.junit.Assert.*;

/**
 * @author Rod Johnson

 * @author Sam Brannen
 */
public class PersistenceInjectionIntegrationTests extends AbstractEntityManagerFactoryIntegrationTests {

	@Autowired
	private DefaultPublicPersistenceContextSetter defaultSetterInjected;

	@Autowired
	private DefaultPublicPersistenceUnitSetterNamedPerson namedSetterInjected;


	@Test
	public void testDefaultPersistenceContextSetterInjection() {
		assertNotNull(defaultSetterInjected.getEntityManager());
	}

	@Test
	public void testSetterInjectionOfNamedPersistenceContext() {
		assertNotNull(namedSetterInjected.getEntityManagerFactory());
	}

}
