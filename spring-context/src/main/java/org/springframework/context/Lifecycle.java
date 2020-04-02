

package org.springframework.context;

/**
 * A common interface defining methods for start/stop lifecycle control.
 * The typical use case for this is to control asynchronous processing.
 * <b>NOTE: This interface does not imply specific auto-startup semantics.
 * Consider implementing {@link SmartLifecycle} for that purpose.</b>
 *
 * Can be implemented by both components (typically a Spring bean defined in a Spring context) and containers  (typically a Spring {@link ApplicationContext} itself).
 * Containers will propagate start/stop signals to all components that apply within each container, e.g. for a stop/restart scenario at runtime.
 *
 * Can be used for direct invocations or for management operations via JMX.
 * In the latter case, the {@link org.springframework.jmx.export.MBeanExporter} will typically be defined with an
 * {@link org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler},restricting the visibility of activity-controlled components to the Lifecycle interface.
 *
 * Note that the present {@code Lifecycle} interface is only supported on <b>top-level singleton beans</b>.
 * On any other component, the {@code Lifecycle} interface will remain undetected and hence ignored. Also, note that the extended
 * {@link SmartLifecycle} interface provides sophisticated integration with the application context's startup and shutdown phases.
 * @since 2.0
 * @see SmartLifecycle
 * @see ConfigurableApplicationContext
 * @see org.springframework.jms.listener.AbstractMessageListenerContainer
 * @see org.springframework.scheduling.quartz.SchedulerFactoryBean
 */
public interface Lifecycle {

	/**
	 * Start this component.
	 * Should not throw an exception if the component is already running.
	 * In the case of a container, this will propagate the start signal to all components that apply.
	 * @see SmartLifecycle#isAutoStartup()
	 */
	void start();

	/**
	 * Stop this component, typically in a synchronous fashion, such that the component is fully stopped upon return of this method.
	 * Consider implementing {@link SmartLifecycle} and its {@code stop(Runnable)} variant when asynchronous stop behavior is necessary.
	 * Note that this stop notification is not guaranteed to come before destruction:
	 * On regular shutdown, {@code Lifecycle} beans will first receive a stop notification before the general destruction callbacks are being propagated; however,
	 * on hot refresh during a context's lifetime or on aborted refresh attempts, a given bean's
	 * destroy method will be called without any consideration of stop signals upfront.
	 * Should not throw an exception if the component is not running (not started yet).
	 * In the case of a container, this will propagate the stop signal to all components that apply.
	 * @see SmartLifecycle#stop(Runnable)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	void stop();

	/**
	 * Check whether this component is currently running.
	 * In the case of a container, this will return {@code true} only if <i>all</i> components that apply are currently running.
	 * @return whether the component is currently running
	 */
	boolean isRunning();

}
