

package org.springframework.scheduling.config;

/**
 * Specialization of {@link IntervalTask} for fixed-delay semantics.
 *

 * @since 5.0.2
 * @see org.springframework.scheduling.annotation.Scheduled#fixedDelay()
 * @see ScheduledTaskRegistrar#addFixedDelayTask(IntervalTask)
 */
public class FixedDelayTask extends IntervalTask {

	/**
	 * Create a new {@code FixedDelayTask}.
	 * @param runnable the underlying task to execute
	 * @param interval how often in milliseconds the task should be executed
	 * @param initialDelay the initial delay before first execution of the task
	 */
	public FixedDelayTask(Runnable runnable, long interval, long initialDelay) {
		super(runnable, interval, initialDelay);
	}

}
