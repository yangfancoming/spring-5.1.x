

package org.springframework.scheduling.quartz;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerConfigException;
import org.quartz.spi.ThreadPool;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Quartz {@link ThreadPool} adapter that delegates to a Spring-managed
 * {@link Executor} instance, specified on {@link SchedulerFactoryBean}.
 *

 * @since 2.0
 * @see SchedulerFactoryBean#setTaskExecutor
 */
public class LocalTaskExecutorThreadPool implements ThreadPool {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private Executor taskExecutor;


	@Override
	public void setInstanceId(String schedInstId) {
	}

	@Override
	public void setInstanceName(String schedName) {
	}


	@Override
	public void initialize() throws SchedulerConfigException {
		// Absolutely needs thread-bound Executor to initialize.
		this.taskExecutor = SchedulerFactoryBean.getConfigTimeTaskExecutor();
		if (this.taskExecutor == null) {
			throw new SchedulerConfigException("No local Executor found for configuration - " +
					"'taskExecutor' property must be set on SchedulerFactoryBean");
		}
	}

	@Override
	public void shutdown(boolean waitForJobsToComplete) {
	}

	@Override
	public int getPoolSize() {
		return -1;
	}


	@Override
	public boolean runInThread(Runnable runnable) {
		Assert.state(this.taskExecutor != null, "No TaskExecutor available");
		try {
			this.taskExecutor.execute(runnable);
			return true;
		}
		catch (RejectedExecutionException ex) {
			logger.error("Task has been rejected by TaskExecutor", ex);
			return false;
		}
	}

	@Override
	public int blockForAvailableThreads() {
		// The present implementation always returns 1, making Quartz
		// always schedule any tasks that it feels like scheduling.
		// This could be made smarter for specific TaskExecutors,
		// for example calling {@code getMaximumPoolSize() - getActiveCount()}
		// on a {@code java.util.concurrent.ThreadPoolExecutor}.
		return 1;
	}

}
