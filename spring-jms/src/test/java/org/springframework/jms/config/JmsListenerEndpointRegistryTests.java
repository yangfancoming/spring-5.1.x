

package org.springframework.jms.config;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class JmsListenerEndpointRegistryTests {

	private final JmsListenerEndpointRegistry registry = new JmsListenerEndpointRegistry();

	private final JmsListenerContainerTestFactory containerFactory = new JmsListenerContainerTestFactory();


	@Rule
	public final ExpectedException thrown = ExpectedException.none();


	@Test
	public void createWithNullEndpoint() {
		thrown.expect(IllegalArgumentException.class);
		registry.registerListenerContainer(null, containerFactory);
	}

	@Test
	public void createWithNullEndpointId() {
		thrown.expect(IllegalArgumentException.class);
		registry.registerListenerContainer(new SimpleJmsListenerEndpoint(), containerFactory);
	}

	@Test
	public void createWithNullContainerFactory() {
		thrown.expect(IllegalArgumentException.class);
		registry.registerListenerContainer(createEndpoint("foo", "myDestination"), null);
	}

	@Test
	public void createWithDuplicateEndpointId() {
		registry.registerListenerContainer(createEndpoint("test", "queue"), containerFactory);

		thrown.expect(IllegalStateException.class);
		registry.registerListenerContainer(createEndpoint("test", "queue"), containerFactory);
	}


	private SimpleJmsListenerEndpoint createEndpoint(String id, String destinationName) {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId(id);
		endpoint.setDestination(destinationName);
		return endpoint;
	}

}
