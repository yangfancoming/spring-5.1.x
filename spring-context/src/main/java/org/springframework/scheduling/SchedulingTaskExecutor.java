

package org.springframework.scheduling;

import org.springframework.core.task.AsyncTaskExecutor;

/**
 * A {@link org.springframework.core.task.TaskExecutor} extension exposing
 * scheduling characteristics that are relevant to potential task submitters.
 *
 * Scheduling clients are encouraged to submit
 * {@link Runnable Runnables} that match the exposed preferences
 * of the {@code TaskExecutor} implementation in use.
 *
 * Note: {@link SchedulingTaskExecutor} implementations are encouraged to also
 * implement the {@link org.springframework.core.task.AsyncListenableTaskExecutor}
 * interface. This is not required due to the dependency on Spring 4.0's new
 * {@link org.springframework.util.concurrent.ListenableFuture} interface,
 * which would make it impossible for third-party executor implementations
 * to remain compatible with both Spring 4.0 and Spring 3.x.
 *

 * @since 2.0
 * @see SchedulingAwareRunnable
 * @see org.springframework.core.task.TaskExecutor
 * @see org.springframework.scheduling.commonj.WorkManagerTaskExecutor
 */
public interface SchedulingTaskExecutor extends AsyncTaskExecutor {

	/**
	 * Does this {@code TaskExecutor} prefer short-lived tasks over long-lived tasks?
	 * A {@code SchedulingTaskExecutor} implementation can indicate whether it
	 * prefers submitted tasks to perform as little work as they can within a single
	 * task execution. For example, submitted tasks might break a repeated loop into
	 * individual subtasks which submit a follow-up task afterwards (if feasible).
	 * This should be considered a hint. Of course {@code TaskExecutor} clients
	 * are free to ignore this flag and hence the {@code SchedulingTaskExecutor}
	 * interface overall. However, thread pools will usually indicated a preference
	 * for short-lived tasks, allowing for more fine-grained scheduling.
	 * @return {@code true} if this executor prefers short-lived tasks (the default),
	 * {@code false} otherwise (for treatment like a regular {@code TaskExecutor})
	 */
	default boolean prefersShortLivedTasks() {
		return true;
	}

}
