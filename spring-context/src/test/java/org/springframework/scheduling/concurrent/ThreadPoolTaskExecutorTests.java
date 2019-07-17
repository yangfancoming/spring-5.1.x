

package org.springframework.scheduling.concurrent;

import org.springframework.core.task.AsyncListenableTaskExecutor;

/**

 * @since 5.0.5
 */
public class ThreadPoolTaskExecutorTests extends AbstractSchedulingTaskExecutorTests {

	@Override
	protected AsyncListenableTaskExecutor buildExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setThreadNamePrefix(THREAD_NAME_PREFIX);
		executor.setMaxPoolSize(1);
		executor.afterPropertiesSet();
		return executor;
	}

}
