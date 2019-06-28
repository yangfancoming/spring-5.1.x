

package org.springframework.context.support;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * @author Arjen Poutsma
 * @author Sam Brannen
 */
public class SimpleThreadScopeTests {

	private final ApplicationContext applicationContext =
			new ClassPathXmlApplicationContext("simpleThreadScopeTests.xml", getClass());


	@Test
	public void getFromScope() throws Exception {
		String name = "threadScopedObject";
		TestBean bean = this.applicationContext.getBean(name, TestBean.class);
		assertNotNull(bean);
		assertSame(bean, this.applicationContext.getBean(name));
		TestBean bean2 = this.applicationContext.getBean(name, TestBean.class);
		assertSame(bean, bean2);
	}

	@Test
	public void getMultipleInstances() throws Exception {
		// Arrange
		TestBean[] beans = new TestBean[2];
		Thread thread1 = new Thread(() -> beans[0] = applicationContext.getBean("threadScopedObject", TestBean.class));
		Thread thread2 = new Thread(() -> beans[1] = applicationContext.getBean("threadScopedObject", TestBean.class));
		// Act
		thread1.start();
		thread2.start();
		// Assert
		Awaitility.await()
					.atMost(500, TimeUnit.MILLISECONDS)
					.pollInterval(10, TimeUnit.MILLISECONDS)
					.until(() -> (beans[0] != null) && (beans[1] != null));
		assertNotSame(beans[0], beans[1]);
	}

}
