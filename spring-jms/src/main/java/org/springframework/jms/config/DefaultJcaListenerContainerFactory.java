

package org.springframework.jms.config;

import javax.resource.spi.ResourceAdapter;

import org.springframework.jms.listener.endpoint.JmsActivationSpecConfig;
import org.springframework.jms.listener.endpoint.JmsActivationSpecFactory;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.lang.Nullable;

/**
 * A {@link JmsListenerContainerFactory} implementation to build a
 * JCA-based {@link JmsMessageEndpointManager}.
 *
 * @author Stephane Nicoll
 * @since 4.1
 */
public class DefaultJcaListenerContainerFactory extends JmsActivationSpecConfig
		implements JmsListenerContainerFactory<JmsMessageEndpointManager> {

	@Nullable
	private ResourceAdapter resourceAdapter;

	@Nullable
	private JmsActivationSpecFactory activationSpecFactory;

	@Nullable
	private DestinationResolver destinationResolver;

	@Nullable
	private Object transactionManager;

	@Nullable
	private Integer phase;


	/**
	 * @see JmsMessageEndpointManager#setResourceAdapter(ResourceAdapter)
	 */
	public void setResourceAdapter(ResourceAdapter resourceAdapter) {
		this.resourceAdapter = resourceAdapter;
	}

	/**
	 * @see JmsMessageEndpointManager#setActivationSpecFactory(JmsActivationSpecFactory)
	 */
	public void setActivationSpecFactory(JmsActivationSpecFactory activationSpecFactory) {
		this.activationSpecFactory = activationSpecFactory;
	}

	/**
	 * @see JmsMessageEndpointManager#setDestinationResolver(DestinationResolver)
	 */
	public void setDestinationResolver(DestinationResolver destinationResolver) {
		this.destinationResolver = destinationResolver;
	}

	/**
	 * @see JmsMessageEndpointManager#setTransactionManager(Object)
	 */
	public void setTransactionManager(Object transactionManager) {
		this.transactionManager = transactionManager;
	}

	/**
	 * @see JmsMessageEndpointManager#setPhase(int)
	 */
	public void setPhase(int phase) {
		this.phase = phase;
	}


	@Override
	public JmsMessageEndpointManager createListenerContainer(JmsListenerEndpoint endpoint) {
		if (this.destinationResolver != null && this.activationSpecFactory != null) {
			throw new IllegalStateException("Specify either 'activationSpecFactory' or " +
					"'destinationResolver', not both. If you define a dedicated JmsActivationSpecFactory bean, " +
					"specify the custom DestinationResolver there (if possible)");
		}

		JmsMessageEndpointManager instance = createContainerInstance();

		if (this.resourceAdapter != null) {
			instance.setResourceAdapter(this.resourceAdapter);
		}
		if (this.activationSpecFactory != null) {
			instance.setActivationSpecFactory(this.activationSpecFactory);
		}
		if (this.destinationResolver != null) {
			instance.setDestinationResolver(this.destinationResolver);
		}
		if (this.transactionManager != null) {
			instance.setTransactionManager(this.transactionManager);
		}
		if (this.phase != null) {
			instance.setPhase(this.phase);
		}

		instance.setActivationSpecConfig(this);
		endpoint.setupListenerContainer(instance);

		return instance;
	}

	/**
	 * Create an empty container instance.
	 */
	protected JmsMessageEndpointManager createContainerInstance() {
		return new JmsMessageEndpointManager();
	}

}
