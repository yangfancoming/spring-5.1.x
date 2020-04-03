

package org.springframework.scheduling.quartz;

import org.junit.Test;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StopWatch;

import static org.junit.Assert.*;

/**
 * @author Mark Fisher
 * @since 3.0
 */
public class QuartzSchedulerLifecycleTests {

	@Test  // SPR-6354
	public void destroyLazyInitSchedulerWithDefaultShutdownOrderDoesNotHang() {
		ConfigurableApplicationContext context =
				new ClassPathXmlApplicationContext("quartzSchedulerLifecycleTests.xml", getClass());
		assertNotNull(context.getBean("lazyInitSchedulerWithDefaultShutdownOrder"));
		StopWatch sw = new StopWatch();
		sw.start("lazyScheduler");
		context.close();
		sw.stop();
		assertTrue("Quartz Scheduler with lazy-init is hanging on destruction: " +
				sw.getTotalTimeMillis(), sw.getTotalTimeMillis() < 500);
	}

	@Test  // SPR-6354
	public void destroyLazyInitSchedulerWithCustomShutdownOrderDoesNotHang() {
		ConfigurableApplicationContext context =
				new ClassPathXmlApplicationContext("quartzSchedulerLifecycleTests.xml", getClass());
		assertNotNull(context.getBean("lazyInitSchedulerWithCustomShutdownOrder"));
		StopWatch sw = new StopWatch();
		sw.start("lazyScheduler");
		context.close();
		sw.stop();
		assertTrue("Quartz Scheduler with lazy-init is hanging on destruction: " +
				sw.getTotalTimeMillis(), sw.getTotalTimeMillis() < 500);
	}

}
