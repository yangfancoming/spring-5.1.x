

package org.springframework.scheduling.config;

import java.util.Set;

/**
 * Common interface for exposing locally scheduled tasks.
 *

 * @since 5.0.2
 * @see ScheduledTaskRegistrar
 * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
 */
public interface ScheduledTaskHolder {

	/**
	 * Return an overview of the tasks that have been scheduled by this instance.
	 */
	Set<ScheduledTask> getScheduledTasks();

}
