

package org.springframework.scheduling.config;

import org.springframework.util.Assert;

/**
 * Holder class defining a {@code Runnable} to be executed as a task, typically at a
 * scheduled time or interval. See subclass hierarchy for various scheduling approaches.


 * @since 3.2
 */
public class Task {

	private final Runnable runnable;


	/**
	 * Create a new {@code Task}.
	 * @param runnable the underlying task to execute
	 */
	public Task(Runnable runnable) {
		Assert.notNull(runnable, "Runnable must not be null");
		this.runnable = runnable;
	}


	/**
	 * Return the underlying task.
	 */
	public Runnable getRunnable() {
		return this.runnable;
	}


	@Override
	public String toString() {
		return this.runnable.toString();
	}

}
