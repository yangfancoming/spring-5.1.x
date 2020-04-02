

package org.springframework.context.event;

import java.lang.reflect.Constructor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link MethodInterceptor Interceptor} that publishes an
 * {@code ApplicationEvent} to all {@code ApplicationListeners}
 * registered with an {@code ApplicationEventPublisher} after each
 * <i>successful</i> method invocation.
 *
 * Note that this interceptor is only capable of publishing <i>stateless</i>
 * events configured via the
 * {@link #setApplicationEventClass "applicationEventClass"} property.
 *
 * @author Dmitriy Kopylenko

 * @author Rick Evans
 * @see #setApplicationEventClass
 * @see org.springframework.context.ApplicationEvent
 * @see org.springframework.context.ApplicationListener
 * @see org.springframework.context.ApplicationEventPublisher
 * @see org.springframework.context.ApplicationContext
 */
public class EventPublicationInterceptor
		implements MethodInterceptor, ApplicationEventPublisherAware, InitializingBean {

	@Nullable
	private Constructor<?> applicationEventClassConstructor;

	@Nullable
	private ApplicationEventPublisher applicationEventPublisher;


	/**
	 * Set the application event class to publish.
	 * The event class <b>must</b> have a constructor with a single
	 * {@code Object} argument for the event source. The interceptor
	 * will pass in the invoked object.
	 * @throws IllegalArgumentException if the supplied {@code Class} is
	 * {@code null} or if it is not an {@code ApplicationEvent} subclass or
	 * if it does not expose a constructor that takes a single {@code Object} argument
	 */
	public void setApplicationEventClass(Class<?> applicationEventClass) {
		if (ApplicationEvent.class == applicationEventClass ||
				!ApplicationEvent.class.isAssignableFrom(applicationEventClass)) {
			throw new IllegalArgumentException("'applicationEventClass' needs to extend ApplicationEvent");
		}
		try {
			this.applicationEventClassConstructor = applicationEventClass.getConstructor(Object.class);
		}
		catch (NoSuchMethodException ex) {
			throw new IllegalArgumentException("ApplicationEvent class [" +
					applicationEventClass.getName() + "] does not have the required Object constructor: " + ex);
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.applicationEventClassConstructor == null) {
			throw new IllegalArgumentException("Property 'applicationEventClass' is required");
		}
	}


	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Object retVal = invocation.proceed();

		Assert.state(this.applicationEventClassConstructor != null, "No ApplicationEvent class set");
		ApplicationEvent event = (ApplicationEvent)
				this.applicationEventClassConstructor.newInstance(invocation.getThis());

		Assert.state(this.applicationEventPublisher != null, "No ApplicationEventPublisher available");
		this.applicationEventPublisher.publishEvent(event);

		return retVal;
	}

}
