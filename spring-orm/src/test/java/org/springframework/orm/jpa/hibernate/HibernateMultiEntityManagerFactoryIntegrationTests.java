

package org.springframework.orm.jpa.hibernate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.AbstractContainerEntityManagerFactoryIntegrationTests;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;

import static org.junit.Assert.*;

/**
 * Hibernate-specific JPA tests with multiple EntityManagerFactory instances.
 *

 */
public class HibernateMultiEntityManagerFactoryIntegrationTests extends AbstractContainerEntityManagerFactoryIntegrationTests {

	@Autowired
	private EntityManagerFactory entityManagerFactory2;


	@Override
	protected String[] getConfigLocations() {
		return new String[] {"/org/springframework/orm/jpa/hibernate/hibernate-manager-multi.xml",
				"/org/springframework/orm/jpa/memdb.xml"};
	}


	@Test
	public void testEntityManagerFactoryImplementsEntityManagerFactoryInfo() {
		assertTrue("Must have introduced config interface", this.entityManagerFactory instanceof EntityManagerFactoryInfo);
		EntityManagerFactoryInfo emfi = (EntityManagerFactoryInfo) this.entityManagerFactory;
		assertEquals("Drivers", emfi.getPersistenceUnitName());
		assertNotNull("PersistenceUnitInfo must be available", emfi.getPersistenceUnitInfo());
		assertNotNull("Raw EntityManagerFactory must be available", emfi.getNativeEntityManagerFactory());
	}

	@Test
	public void testEntityManagerFactory2() {
		EntityManager em = this.entityManagerFactory2.createEntityManager();
		try {
			em.createQuery("select tb from TestBean");
			fail("Should have thrown IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}
		finally {
			em.close();
		}
	}

}
