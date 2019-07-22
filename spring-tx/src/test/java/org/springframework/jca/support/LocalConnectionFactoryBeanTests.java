

package org.springframework.jca.support;

import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ManagedConnectionFactory;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for the {@link LocalConnectionFactoryBean} class.
 *
 * @author Rick Evans

 */
public class LocalConnectionFactoryBeanTests {

	@Test(expected = IllegalArgumentException.class)
	public void testManagedConnectionFactoryIsRequired() throws Exception {
		new LocalConnectionFactoryBean().afterPropertiesSet();
	}

	@Test
	public void testIsSingleton() throws Exception {
		LocalConnectionFactoryBean factory = new LocalConnectionFactoryBean();
		assertTrue(factory.isSingleton());
	}

	@Test
	public void testGetObjectTypeIsNullIfConnectionFactoryHasNotBeenConfigured() throws Exception {
		LocalConnectionFactoryBean factory = new LocalConnectionFactoryBean();
		assertNull(factory.getObjectType());
	}

	@Test
	public void testCreatesVanillaConnectionFactoryIfNoConnectionManagerHasBeenConfigured() throws Exception {
		final Object CONNECTION_FACTORY = new Object();
		ManagedConnectionFactory managedConnectionFactory = mock(ManagedConnectionFactory.class);
		given(managedConnectionFactory.createConnectionFactory()).willReturn(CONNECTION_FACTORY);
		LocalConnectionFactoryBean factory = new LocalConnectionFactoryBean();
		factory.setManagedConnectionFactory(managedConnectionFactory);
		factory.afterPropertiesSet();
		assertEquals(CONNECTION_FACTORY, factory.getObject());
	}

	@Test
	public void testCreatesManagedConnectionFactoryIfAConnectionManagerHasBeenConfigured() throws Exception {
		ManagedConnectionFactory managedConnectionFactory = mock(ManagedConnectionFactory.class);
		ConnectionManager connectionManager = mock(ConnectionManager.class);
		LocalConnectionFactoryBean factory = new LocalConnectionFactoryBean();
		factory.setManagedConnectionFactory(managedConnectionFactory);
		factory.setConnectionManager(connectionManager);
		factory.afterPropertiesSet();
		verify(managedConnectionFactory).createConnectionFactory(connectionManager);
	}

}
