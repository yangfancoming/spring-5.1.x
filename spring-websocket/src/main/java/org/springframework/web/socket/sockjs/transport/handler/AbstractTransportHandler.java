

package org.springframework.web.socket.sockjs.transport.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;
import org.springframework.web.socket.sockjs.transport.TransportHandler;

/**
 * Common base class for {@link TransportHandler} implementations.
 *
 *
 * @since 4.0
 */
public abstract class AbstractTransportHandler implements TransportHandler {

	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private SockJsServiceConfig serviceConfig;


	@Override
	public void initialize(SockJsServiceConfig serviceConfig) {
		this.serviceConfig = serviceConfig;
	}

	public SockJsServiceConfig getServiceConfig() {
		Assert.state(this.serviceConfig != null, "No SockJsServiceConfig available");
		return this.serviceConfig;
	}

}
