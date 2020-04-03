

package org.springframework.orm.jpa.eclipselink;

import org.eclipse.persistence.jpa.JpaEntityManager;
import org.junit.Test;

import org.springframework.orm.jpa.AbstractContainerEntityManagerFactoryIntegrationTests;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;

import static org.junit.Assert.*;

/**
 * EclipseLink-specific JPA tests.
 *

 */
public class EclipseLinkEntityManagerFactoryIntegrationTests extends AbstractContainerEntityManagerFactoryIntegrationTests {

	@Test
	public void testCanCastNativeEntityManagerFactoryToEclipseLinkEntityManagerFactoryImpl() {
		EntityManagerFactoryInfo emfi = (EntityManagerFactoryInfo) entityManagerFactory;
		assertTrue(emfi.getNativeEntityManagerFactory().getClass().getName().endsWith("EntityManagerFactoryImpl"));
	}

	@Test
	public void testCanCastSharedEntityManagerProxyToEclipseLinkEntityManager() {
		assertTrue(sharedEntityManager instanceof JpaEntityManager);
		JpaEntityManager eclipselinkEntityManager = (JpaEntityManager) sharedEntityManager;
		assertNotNull(eclipselinkEntityManager.getActiveSession());
	}

}
