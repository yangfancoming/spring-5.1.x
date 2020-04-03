

package org.springframework.scheduling.quartz;

import org.quartz.SchedulerContext;

import org.springframework.beans.factory.Aware;

/**
 * Callback interface to be implemented by Spring-managed
 * Quartz artifacts that need access to the SchedulerContext
 * (without having natural access to it).
 *
 * xmlBeanDefinitionReaderCurrently only supported for custom JobFactory implementations
 * that are passed in via Spring's SchedulerFactoryBean.
 *


 * @since 2.0
 * @see org.quartz.spi.JobFactory
 * @see SchedulerFactoryBean#setJobFactory
 */
public interface SchedulerContextAware extends Aware {

	/**
	 * Set the SchedulerContext of the current Quartz Scheduler.
	 * @see org.quartz.Scheduler#getContext()
	 */
	void setSchedulerContext(SchedulerContext schedulerContext);

}
