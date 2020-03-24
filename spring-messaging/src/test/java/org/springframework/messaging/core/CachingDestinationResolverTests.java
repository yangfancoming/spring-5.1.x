

package org.springframework.messaging.core;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for {@link CachingDestinationResolverProxy}.
 *
 * @author Agim Emruli

 */
public class CachingDestinationResolverTests {

	@Test
	public void cachedDestination() {
		@SuppressWarnings("unchecked")
		DestinationResolver<String> resolver = mock(DestinationResolver.class);
		CachingDestinationResolverProxy<String> resolverProxy = new CachingDestinationResolverProxy<>(resolver);

		given(resolver.resolveDestination("abcd")).willReturn("dcba");
		given(resolver.resolveDestination("1234")).willReturn("4321");

		assertEquals("dcba", resolverProxy.resolveDestination("abcd"));
		assertEquals("4321", resolverProxy.resolveDestination("1234"));
		assertEquals("4321", resolverProxy.resolveDestination("1234"));
		assertEquals("dcba", resolverProxy.resolveDestination("abcd"));

		verify(resolver, times(1)).resolveDestination("abcd");
		verify(resolver, times(1)).resolveDestination("1234");
	}

	@Test(expected = IllegalArgumentException.class)
	public void noTargetSet() {
		CachingDestinationResolverProxy<String> resolverProxy = new CachingDestinationResolverProxy<>();
		resolverProxy.afterPropertiesSet();
	}

	@Test(expected = IllegalArgumentException.class)
	public void nullTargetThroughConstructor() {
		new CachingDestinationResolverProxy<String>(null);
	}

}
