

package org.springframework.scheduling.concurrent;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.NoOpRunnable;


public class ConcurrentTaskExecutorTests extends AbstractSchedulingTaskExecutorTests {

	private final ThreadPoolExecutor concurrentExecutor =
			new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());


	@Override
	protected AsyncListenableTaskExecutor buildExecutor() {
		concurrentExecutor.setThreadFactory(new CustomizableThreadFactory(THREAD_NAME_PREFIX));
		return new ConcurrentTaskExecutor(concurrentExecutor);
	}

	@Override
	public void shutdownExecutor() {
		List<Runnable> remainingTasks = concurrentExecutor.shutdownNow();
		for (Runnable task : remainingTasks) {
			if (task instanceof RunnableFuture) {
				((RunnableFuture<?>) task).cancel(true);
			}
		}
	}


	@Test
	public void zeroArgCtorResultsInDefaultTaskExecutorBeingUsed() {
		ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor();
		// must not throw a NullPointerException
		executor.execute(new NoOpRunnable());
	}

	@Test
	public void passingNullExecutorToCtorResultsInDefaultTaskExecutorBeingUsed() {
		ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor(null);
		// must not throw a NullPointerException
		executor.execute(new NoOpRunnable());
	}

	@Test
	public void passingNullExecutorToSetterResultsInDefaultTaskExecutorBeingUsed() {
		ConcurrentTaskExecutor executor = new ConcurrentTaskExecutor();
		executor.setConcurrentExecutor(null);
		// must not throw a NullPointerException
		executor.execute(new NoOpRunnable());
	}

}
