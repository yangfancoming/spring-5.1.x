

package org.springframework.scheduling.concurrent;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.support.DelegatingErrorHandlingRunnable;
import org.springframework.scheduling.support.TaskUtils;

/**

 * @since 5.0.5
 */
public class DecoratedThreadPoolTaskExecutorTests extends AbstractSchedulingTaskExecutorTests {

	@Override
	protected AsyncListenableTaskExecutor buildExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setTaskDecorator(runnable ->
				new DelegatingErrorHandlingRunnable(runnable, TaskUtils.LOG_AND_PROPAGATE_ERROR_HANDLER));
		executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
		executor.setMaxPoolSize(1);
		executor.afterPropertiesSet();
		return executor;
	}

}
