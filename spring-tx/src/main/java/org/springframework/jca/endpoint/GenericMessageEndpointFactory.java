

package org.springframework.jca.endpoint;

import javax.resource.ResourceException;
import javax.resource.spi.UnavailableException;
import javax.resource.spi.endpoint.MessageEndpoint;
import javax.transaction.xa.XAResource;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Generic implementation of the JCA 1.7
 * {@link javax.resource.spi.endpoint.MessageEndpointFactory} interface,
 * providing transaction management capabilities for any kind of message
 * listener object (e.g. {@link javax.jms.MessageListener} objects or
 * {@link javax.resource.cci.MessageListener} objects.
 *
 * Uses AOP proxies for concrete endpoint instances, simply wrapping
 * the specified message listener object and exposing all of its implemented
 * interfaces on the endpoint instance.
 *
 * Typically used with Spring's {@link GenericMessageEndpointManager},
 * but not tied to it. As a consequence, this endpoint factory could
 * also be used with programmatic endpoint management on a native {@link javax.resource.spi.ResourceAdapter} instance.
 * @since 2.5
 * @see #setMessageListener
 * @see #setTransactionManager
 * @see GenericMessageEndpointManager
 */
public class GenericMessageEndpointFactory extends AbstractMessageEndpointFactory {

	@Nullable
	private Object messageListener;

	/**
	 * Specify the message listener object that the endpoint should expose (e.g. a {@link javax.jms.MessageListener} objects or
	 * {@link javax.resource.cci.MessageListener} implementation).
	 */
	public void setMessageListener(Object messageListener) {
		this.messageListener = messageListener;
	}

	/**
	 * Return the message listener object for this endpoint.
	 * @since 5.0
	 */
	protected Object getMessageListener() {
		Assert.state(this.messageListener != null, "No message listener set");
		return this.messageListener;
	}

	/**
	 * Wrap each concrete endpoint instance with an AOP proxy, exposing the message listener's interfaces as well as the endpoint SPI through an AOP introduction.
	 */
	@Override
	public MessageEndpoint createEndpoint(XAResource xaResource) throws UnavailableException {
		GenericMessageEndpoint endpoint = (GenericMessageEndpoint) super.createEndpoint(xaResource);
		ProxyFactory proxyFactory = new ProxyFactory(getMessageListener());
		DelegatingIntroductionInterceptor introduction = new DelegatingIntroductionInterceptor(endpoint);
		introduction.suppressInterface(MethodInterceptor.class);
		proxyFactory.addAdvice(introduction);
		return (MessageEndpoint) proxyFactory.getProxy();
	}

	/**
	 * Creates a concrete generic message endpoint, internal to this factory.
	 */
	@Override
	protected AbstractMessageEndpoint createEndpointInternal() throws UnavailableException {
		return new GenericMessageEndpoint();
	}


	/**
	 * Private inner class that implements the concrete generic message endpoint,as an AOP Alliance MethodInterceptor that will be invoked by a proxy.
	 */
	private class GenericMessageEndpoint extends AbstractMessageEndpoint implements MethodInterceptor {

		@Override
		public Object invoke(MethodInvocation methodInvocation) throws Throwable {
			Throwable endpointEx = null;
			boolean applyDeliveryCalls = !hasBeforeDeliveryBeenCalled();
			if (applyDeliveryCalls) {
				try {
					beforeDelivery(null);
				}catch (ResourceException ex) {
					throw adaptExceptionIfNecessary(methodInvocation, ex);
				}
			}
			try {
				return methodInvocation.proceed();
			}catch (Throwable ex) {
				endpointEx = ex;
				onEndpointException(ex);
				throw ex;
			}finally {
				if (applyDeliveryCalls) {
					try {
						afterDelivery();
					}catch (ResourceException ex) {
						if (endpointEx == null) throw adaptExceptionIfNecessary(methodInvocation, ex);
					}
				}
			}
		}

		private Exception adaptExceptionIfNecessary(MethodInvocation methodInvocation, ResourceException ex) {
			if (ReflectionUtils.declaresException(methodInvocation.getMethod(), ex.getClass())) {
				return ex;
			}else {
				return new InternalResourceException(ex);
			}
		}

		@Override
		protected ClassLoader getEndpointClassLoader() {
			return getMessageListener().getClass().getClassLoader();
		}
	}

	/**
	 * Internal exception thrown when a ResourceException has been encountered during the endpoint invocation.
	 * Will only be used if the ResourceAdapter does not invoke the endpoint's {@code beforeDelivery} and {@code afterDelivery}
	 * directly, leaving it up to the concrete endpoint to apply those -  and to handle any ResourceExceptions thrown from them.
	 */
	@SuppressWarnings("serial")
	public static class InternalResourceException extends RuntimeException {

		public InternalResourceException(ResourceException cause) {
			super(cause);
		}
	}

}
