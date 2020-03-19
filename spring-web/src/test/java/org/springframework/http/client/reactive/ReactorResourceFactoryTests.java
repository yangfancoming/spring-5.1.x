
package org.springframework.http.client.reactive;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import reactor.netty.http.HttpResources;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ReactorResourceFactory}.
 */
public class ReactorResourceFactoryTests {

	private final ReactorResourceFactory resourceFactory = new ReactorResourceFactory();

	private final ConnectionProvider connectionProvider = mock(ConnectionProvider.class);

	private final LoopResources loopResources = mock(LoopResources.class);


	@Test
	public void globalResources() {

		this.resourceFactory.setUseGlobalResources(true);
		this.resourceFactory.afterPropertiesSet();

		HttpResources globalResources = HttpResources.get();
		assertSame(globalResources, this.resourceFactory.getConnectionProvider());
		assertSame(globalResources, this.resourceFactory.getLoopResources());
		assertFalse(globalResources.isDisposed());

		this.resourceFactory.destroy();

		assertTrue(globalResources.isDisposed());
	}

	@Test
	public void globalResourcesWithConsumer() {

		AtomicBoolean invoked = new AtomicBoolean(false);

		this.resourceFactory.addGlobalResourcesConsumer(httpResources -> invoked.set(true));
		this.resourceFactory.afterPropertiesSet();

		assertTrue(invoked.get());
		this.resourceFactory.destroy();
	}

	@Test
	public void localResources() {

		this.resourceFactory.setUseGlobalResources(false);
		this.resourceFactory.afterPropertiesSet();

		ConnectionProvider connectionProvider = this.resourceFactory.getConnectionProvider();
		LoopResources loopResources = this.resourceFactory.getLoopResources();

		assertNotSame(HttpResources.get(), connectionProvider);
		assertNotSame(HttpResources.get(), loopResources);

		// The below does not work since ConnectionPoolProvider simply checks if pool is empty.
		// assertFalse(connectionProvider.isDisposed());
		assertFalse(loopResources.isDisposed());

		this.resourceFactory.destroy();

		assertTrue(connectionProvider.isDisposed());
		assertTrue(loopResources.isDisposed());
	}

	@Test
	public void localResourcesViaSupplier() {

		this.resourceFactory.setUseGlobalResources(false);
		this.resourceFactory.setConnectionProviderSupplier(() -> this.connectionProvider);
		this.resourceFactory.setLoopResourcesSupplier(() -> this.loopResources);
		this.resourceFactory.afterPropertiesSet();

		ConnectionProvider connectionProvider = this.resourceFactory.getConnectionProvider();
		LoopResources loopResources = this.resourceFactory.getLoopResources();

		assertSame(this.connectionProvider, connectionProvider);
		assertSame(this.loopResources, loopResources);

		verifyNoMoreInteractions(this.connectionProvider, this.loopResources);

		this.resourceFactory.destroy();

		// Managed (destroy disposes)..
		verify(this.connectionProvider).dispose();
		verify(this.loopResources).dispose();
		verifyNoMoreInteractions(this.connectionProvider, this.loopResources);
	}

	@Test
	public void externalResources() {

		this.resourceFactory.setUseGlobalResources(false);
		this.resourceFactory.setConnectionProvider(this.connectionProvider);
		this.resourceFactory.setLoopResources(this.loopResources);
		this.resourceFactory.afterPropertiesSet();

		ConnectionProvider connectionProvider = this.resourceFactory.getConnectionProvider();
		LoopResources loopResources = this.resourceFactory.getLoopResources();

		assertSame(this.connectionProvider, connectionProvider);
		assertSame(this.loopResources, loopResources);

		verifyNoMoreInteractions(this.connectionProvider, this.loopResources);

		this.resourceFactory.destroy();

		// Not managed (destroy has no impact)..
		verifyNoMoreInteractions(this.connectionProvider, this.loopResources);
	}

}
