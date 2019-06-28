

package org.springframework.scheduling.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.junit.Assert.*;

/**
 * @author Juergen Hoeller
 */
public class ThreadPoolExecutorFactoryBeanTests {

	@Test
	public void defaultExecutor() throws Exception {
		ApplicationContext context = new AnnotationConfigApplicationContext(ExecutorConfig.class);
		ExecutorService executor = context.getBean("executor", ExecutorService.class);

		FutureTask<String> task = new FutureTask<>(new Callable<String>() {
			@Override
			public String call() throws Exception {
				return "foo";
			}
		});
		executor.execute(task);
		assertEquals("foo", task.get());
	}


	@Configuration
	public static class ExecutorConfig {

		@Bean
		public ThreadPoolExecutorFactoryBean executorFactory() {
			return new ThreadPoolExecutorFactoryBean();
		}

		@Bean
		public ExecutorService executor() {
			return executorFactory().getObject();
		}
	}

}
