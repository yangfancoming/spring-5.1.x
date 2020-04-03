

package org.springframework.orm.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.spi.PersistenceUnitInfo;

import org.junit.After;
import org.junit.Before;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Superclass for unit tests for EntityManagerFactory-creating beans.
 * Note: Subclasses must set expectations on the mock EntityManagerFactory.
 *
 * @author Rod Johnson

 * @author Phillip Webb
 */
public abstract class AbstractEntityManagerFactoryBeanTests {

	protected static EntityManagerFactory mockEmf;

	@Before
	public void setUp() throws Exception {
		mockEmf = mock(EntityManagerFactory.class);
	}

	@After
	public void tearDown() throws Exception {
		assertTrue(TransactionSynchronizationManager.getResourceMap().isEmpty());
		assertFalse(TransactionSynchronizationManager.isSynchronizationActive());
		assertFalse(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
		assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
	}

	protected void checkInvariants(AbstractEntityManagerFactoryBean demf) {
		assertTrue(EntityManagerFactory.class.isAssignableFrom(demf.getObjectType()));
		Object gotObject = demf.getObject();
		assertTrue("Object created by factory implements EntityManagerFactoryInfo",
				gotObject instanceof EntityManagerFactoryInfo);
		EntityManagerFactoryInfo emfi = (EntityManagerFactoryInfo) demf.getObject();
		assertSame("Successive invocations of getObject() return same object", emfi, demf.getObject());
		assertSame(emfi, demf.getObject());
		assertSame(emfi.getNativeEntityManagerFactory(), mockEmf);
	}


	protected static class DummyEntityManagerFactoryBean extends AbstractEntityManagerFactoryBean {

		private static final long serialVersionUID = 1L;

		private final EntityManagerFactory emf;

		public DummyEntityManagerFactoryBean(EntityManagerFactory emf) {
			this.emf = emf;
		}

		@Override
		protected EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException {
			return emf;
		}

		@Override
		public PersistenceUnitInfo getPersistenceUnitInfo() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getPersistenceUnitName() {
			return "test";
		}
	}

}
