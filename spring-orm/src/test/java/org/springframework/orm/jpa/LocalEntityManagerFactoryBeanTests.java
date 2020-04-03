

package org.springframework.orm.jpa;

import java.util.Map;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.ProviderUtil;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * @author Rod Johnson
 * @author Phillip Webb
 */
@SuppressWarnings("rawtypes")
public class LocalEntityManagerFactoryBeanTests extends AbstractEntityManagerFactoryBeanTests {

	// Static fields set by inner class DummyPersistenceProvider

	private static String actualName;

	private static Map actualProps;

	@After
	public void verifyClosed() throws Exception {
		verify(mockEmf).close();
	}

	@Test
	public void testValidUsageWithDefaultProperties() throws Exception {
		testValidUsage(null);
	}

	@Test
	public void testValidUsageWithExplicitProperties() throws Exception {
		testValidUsage(new Properties());
	}

	protected void testValidUsage(Properties props) throws Exception {
		// This will be set by DummyPersistenceProvider
		actualName = null;
		actualProps = null;

		LocalEntityManagerFactoryBean lemfb = new LocalEntityManagerFactoryBean();
		String entityManagerName = "call me Bob";

		lemfb.setPersistenceUnitName(entityManagerName);
		lemfb.setPersistenceProviderClass(DummyPersistenceProvider.class);
		if (props != null) {
			lemfb.setJpaProperties(props);
		}
		lemfb.afterPropertiesSet();

		assertSame(entityManagerName, actualName);
		if (props != null) {
			assertEquals(props, actualProps);
		}
		checkInvariants(lemfb);

		lemfb.destroy();
	}


	protected static class DummyPersistenceProvider implements PersistenceProvider {

		@Override
		public EntityManagerFactory createContainerEntityManagerFactory(PersistenceUnitInfo pui, Map map) {
			throw new UnsupportedOperationException();
		}

		@Override
		public EntityManagerFactory createEntityManagerFactory(String emfName, Map properties) {
			actualName = emfName;
			actualProps = properties;
			return mockEmf;
		}

		@Override
		public ProviderUtil getProviderUtil() {
			throw new UnsupportedOperationException();
		}

		// JPA 2.1 method
		public void generateSchema(PersistenceUnitInfo persistenceUnitInfo, Map map) {
			throw new UnsupportedOperationException();
		}

		// JPA 2.1 method
		public boolean generateSchema(String persistenceUnitName, Map map) {
			throw new UnsupportedOperationException();
		}
	}

}
