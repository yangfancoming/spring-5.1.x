

package org.springframework.scheduling.config;

/**
 * Specialization of {@link IntervalTask} for fixed-rate semantics.
 *

 * @since 5.0.2
 * @see org.springframework.scheduling.annotation.Scheduled#fixedRate()
 * @see ScheduledTaskRegistrar#addFixedRateTask(IntervalTask)
 */
public class FixedRateTask extends IntervalTask {

	/**
	 * Create a new {@code FixedRateTask}.
	 * @param runnable the underlying task to execute
	 * @param interval how often in milliseconds the task should be executed
	 * @param initialDelay the initial delay before first execution of the task
	 */
	public FixedRateTask(Runnable runnable, long interval, long initialDelay) {
		super(runnable, interval, initialDelay);
	}

}
