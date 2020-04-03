

package org.springframework.jms.support.destination;

import javax.jms.Destination;
import javax.jms.Session;
import javax.naming.NamingException;

import org.junit.Test;

import org.springframework.jms.StubTopic;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;


public class JndiDestinationResolverTests {

	private static final String DESTINATION_NAME = "foo";

	private static final Destination DESTINATION = new StubTopic();


	@Test
	public void testHitsCacheSecondTimeThrough() throws Exception {

		Session session = mock(Session.class);

		JndiDestinationResolver resolver = new OneTimeLookupJndiDestinationResolver();
		Destination destination = resolver.resolveDestinationName(session, DESTINATION_NAME, true);
		assertNotNull(destination);
		assertSame(DESTINATION, destination);
	}

	@Test
	public void testDoesNotUseCacheIfCachingIsTurnedOff() throws Exception {

		Session session = mock(Session.class);

		CountingCannedJndiDestinationResolver resolver
				= new CountingCannedJndiDestinationResolver();
		resolver.setCache(false);
		Destination destination = resolver.resolveDestinationName(session, DESTINATION_NAME, true);
		assertNotNull(destination);
		assertSame(DESTINATION, destination);
		assertEquals(1, resolver.getCallCount());

		destination = resolver.resolveDestinationName(session, DESTINATION_NAME, true);
		assertNotNull(destination);
		assertSame(DESTINATION, destination);
		assertEquals(2, resolver.getCallCount());
	}

	@Test
	public void testDelegatesToFallbackIfNotResolvedInJndi() throws Exception {
		Session session = mock(Session.class);

		DestinationResolver dynamicResolver = mock(DestinationResolver.class);
		given(dynamicResolver.resolveDestinationName(session, DESTINATION_NAME,
				true)).willReturn(DESTINATION);

		JndiDestinationResolver resolver = new JndiDestinationResolver() {
			@Override
			protected <T> T lookup(String jndiName, Class<T> requiredClass) throws NamingException {
				throw new NamingException();
			}
		};
		resolver.setFallbackToDynamicDestination(true);
		resolver.setDynamicDestinationResolver(dynamicResolver);
		Destination destination = resolver.resolveDestinationName(session, DESTINATION_NAME, true);

		assertNotNull(destination);
		assertSame(DESTINATION, destination);
	}

	@Test
	public void testDoesNotDelegateToFallbackIfNotResolvedInJndi() throws Exception {
		final Session session = mock(Session.class);
		DestinationResolver dynamicResolver = mock(DestinationResolver.class);

		final JndiDestinationResolver resolver = new JndiDestinationResolver() {
			@Override
			protected <T> T lookup(String jndiName, Class<T> requiredClass) throws NamingException {
				throw new NamingException();
			}
		};
		resolver.setDynamicDestinationResolver(dynamicResolver);

		try {
			resolver.resolveDestinationName(session, DESTINATION_NAME, true);
			fail("expected DestinationResolutionException");
		}
		catch (DestinationResolutionException ex) {
			// expected
		}
	}


	private static class OneTimeLookupJndiDestinationResolver extends JndiDestinationResolver {

		private boolean called;

		@Override
		protected <T> T lookup(String jndiName, Class<T> requiredType) throws NamingException {
			if (called) {
				fail("Must not be delegating to lookup(..), must be resolving from cache.");
			}
			assertEquals(DESTINATION_NAME, jndiName);
			called = true;
			return requiredType.cast(DESTINATION);
		}
	}

	private static class CountingCannedJndiDestinationResolver extends JndiDestinationResolver {

		private int callCount;

		public int getCallCount() {
			return this.callCount;
		}

		@Override
		protected <T> T lookup(String jndiName, Class<T> requiredType) throws NamingException {
			++this.callCount;
			return requiredType.cast(DESTINATION);
		}
	}
}
