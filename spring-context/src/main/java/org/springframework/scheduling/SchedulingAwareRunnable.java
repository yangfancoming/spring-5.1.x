

package org.springframework.scheduling;

/**
 * Extension of the Runnable interface, adding special callbacks
 * for long-running operations.
 *
 * This interface closely corresponds to the CommonJ Work interface,
 * but is kept separate to avoid a required CommonJ dependency.
 *
 * Scheduling-capable TaskExecutors are encouraged to check a submitted
 * Runnable, detecting whether this interface is implemented and reacting
 * as appropriately as they are able to.
 *

 * @since 2.0
 * @see commonj.work.Work
 * @see org.springframework.core.task.TaskExecutor
 * @see SchedulingTaskExecutor
 * @see org.springframework.scheduling.commonj.WorkManagerTaskExecutor
 */
public interface SchedulingAwareRunnable extends Runnable {

	/**
	 * Return whether the Runnable's operation is long-lived
	 * ({@code true}) versus short-lived ({@code false}).
	 * In the former case, the task will not allocate a thread from the thread
	 * pool (if any) but rather be considered as long-running background thread.
	 * This should be considered a hint. Of course TaskExecutor implementations
	 * are free to ignore this flag and the SchedulingAwareRunnable interface overall.
	 */
	boolean isLongLived();

}
