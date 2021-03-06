

package org.springframework.beans.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.tests.Assume;
import org.springframework.tests.TestGroup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.tests.TestResourceUtils.qualifiedResource;

/**
 * @since 10.03.2004
 */
public class ConcurrentBeanFactoryTests {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	private static final Date DATE_1, DATE_2;

	static {
		try {
			DATE_1 = DATE_FORMAT.parse("2004/08/08");
			DATE_2 = DATE_FORMAT.parse("2000/02/02");
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}


	private static final Log logger = LogFactory.getLog(ConcurrentBeanFactoryTests.class);

	private BeanFactory factory;

	private final Set<TestRun> set = Collections.synchronizedSet(new HashSet<>());

	private Throwable ex;


	@Before
	public void setup() throws Exception {
		Assume.group(TestGroup.PERFORMANCE);
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		new XmlBeanDefinitionReader(factory).loadBeanDefinitions(qualifiedResource(ConcurrentBeanFactoryTests.class, "context.xml"));
		factory.addPropertyEditorRegistrar(registry -> registry.registerCustomEditor(Date.class,new CustomDateEditor((DateFormat) DATE_FORMAT.clone(), false)));
		this.factory = factory;
	}


	@Test
	public void testSingleThread() {
		for (int i = 0; i < 100; i++) {
			performTest();
		}
	}

	@Test
	public void testConcurrent() {
		for (int i = 0; i < 100; i++) {
			TestRun run = new TestRun();
			run.setDaemon(true);
			set.add(run);
		}
		for (Iterator<TestRun> it = new HashSet<>(set).iterator(); it.hasNext();) {
			TestRun run = it.next();
			run.start();
		}
		logger.info("Thread creation over, " + set.size() + " still active.");
		synchronized (set) {
			while (!set.isEmpty() && ex == null) {
				try {
					set.wait();
				}
				catch (InterruptedException e) {
					logger.info(e.toString());
				}
				logger.info(set.size() + " threads still active.");
			}
		}
		if (ex != null) {
			fail(ex.getMessage());
		}
	}

	private void performTest() {
		ConcurrentBean b1 = (ConcurrentBean) factory.getBean("bean1");
		ConcurrentBean b2 = (ConcurrentBean) factory.getBean("bean2");

		assertEquals(DATE_1, b1.getDate());
		assertEquals(DATE_2, b2.getDate());
	}


	private class TestRun extends Thread {

		@Override
		public void run() {
			try {
				for (int i = 0; i < 10000; i++) {
					performTest();
				}
			}
			catch (Throwable e) {
				ex = e;
			}
			finally {
				synchronized (set) {
					set.remove(this);
					set.notifyAll();
				}
			}
		}
	}


	public static class ConcurrentBean {

		private Date date;

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}
	}

}
