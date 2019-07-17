

package org.springframework.scheduling.config;

import org.junit.Test;

import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Tests ensuring that tasks scheduled using the <task:scheduled> element
 * are never marked lazy, even if the enclosing <beans> element declares
 * default-lazy-init="true". See  SPR-8498
 *
 * @author Mike Youngstrom

 */
public class LazyScheduledTasksBeanDefinitionParserTests {

	@Test(timeout = 5000)
	public void checkTarget() {
		Task task =
			new GenericXmlApplicationContext(
					LazyScheduledTasksBeanDefinitionParserTests.class,
					"lazyScheduledTasksContext.xml")
				.getBean(Task.class);

		while (!task.executed) {
			try {
				Thread.sleep(10);
			}
			catch (Exception ex) { /* Do Nothing */ }
		}
	}


	static class Task {

		volatile boolean executed = false;

		public void doWork() {
			executed = true;
		}
	}

}
