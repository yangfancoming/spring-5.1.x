

package org.springframework.jms.config;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.beans.factory.support.StaticListableBeanFactory;

import static org.junit.Assert.*;


public class JmsListenerEndpointRegistrarTests {

	private final JmsListenerEndpointRegistrar registrar = new JmsListenerEndpointRegistrar();

	private final JmsListenerEndpointRegistry registry = new JmsListenerEndpointRegistry();

	private final JmsListenerContainerTestFactory containerFactory = new JmsListenerContainerTestFactory();


	@Rule
	public final ExpectedException thrown = ExpectedException.none();


	@Before
	public void setup() {
		this.registrar.setEndpointRegistry(this.registry);
		this.registrar.setBeanFactory(new StaticListableBeanFactory());
	}


	@Test
	public void registerNullEndpoint() {
		this.thrown.expect(IllegalArgumentException.class);
		this.registrar.registerEndpoint(null, this.containerFactory);
	}

	@Test
	public void registerNullEndpointId() {
		this.thrown.expect(IllegalArgumentException.class);
		this.registrar.registerEndpoint(new SimpleJmsListenerEndpoint(), this.containerFactory);
	}

	@Test
	public void registerEmptyEndpointId() {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId("");

		this.thrown.expect(IllegalArgumentException.class);
		this.registrar.registerEndpoint(endpoint, this.containerFactory);
	}

	@Test
	public void registerNullContainerFactoryIsAllowed() throws Exception {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId("some id");
		this.registrar.setContainerFactory(this.containerFactory);
		this.registrar.registerEndpoint(endpoint, null);
		this.registrar.afterPropertiesSet();
		assertNotNull("Container not created", this.registry.getListenerContainer("some id"));
		assertEquals(1, this.registry.getListenerContainers().size());
		assertEquals("some id", this.registry.getListenerContainerIds().iterator().next());
	}

	@Test
	public void registerNullContainerFactoryWithNoDefault() throws Exception {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId("some id");
		this.registrar.registerEndpoint(endpoint, null);

		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage(endpoint.toString());
		this.registrar.afterPropertiesSet();
	}

	@Test
	public void registerContainerWithoutFactory() throws Exception {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId("myEndpoint");
		this.registrar.setContainerFactory(this.containerFactory);
		this.registrar.registerEndpoint(endpoint);
		this.registrar.afterPropertiesSet();
		assertNotNull("Container not created", this.registry.getListenerContainer("myEndpoint"));
		assertEquals(1, this.registry.getListenerContainers().size());
		assertEquals("myEndpoint", this.registry.getListenerContainerIds().iterator().next());
	}

}
