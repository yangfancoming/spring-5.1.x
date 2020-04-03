

package org.springframework.jms.support.destination;

import javax.jms.ConnectionFactory;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;


public class JmsDestinationAccessorTests {

	@Test
	public void testChokesIfDestinationResolverIsetToNullExplicitly() throws Exception {
		ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

		try {
			JmsDestinationAccessor accessor = new StubJmsDestinationAccessor();
			accessor.setConnectionFactory(connectionFactory);
			accessor.setDestinationResolver(null);
			accessor.afterPropertiesSet();
			fail("expected IllegalArgumentException");
		}
		catch (IllegalArgumentException ex) {
			// expected
		}

	}

	@Test
	public void testSessionTransactedModeReallyDoesDefaultToFalse() throws Exception {
		JmsDestinationAccessor accessor = new StubJmsDestinationAccessor();
		assertFalse("The [pubSubDomain] property of JmsDestinationAccessor must default to " +
				"false (i.e. Queues are used by default). Change this test (and the " +
				"attendant Javadoc) if you have changed the default.",
				accessor.isPubSubDomain());
	}


	private static class StubJmsDestinationAccessor extends JmsDestinationAccessor {

	}

}
