

package org.springframework.jmx.export.annotation;

import javax.management.MXBean;

import org.junit.Test;

import org.springframework.jmx.support.JmxUtils;

import static org.junit.Assert.*;


public class JmxUtilsAnnotationTests {

	@Test
	public void notMXBean() throws Exception {
		assertFalse("MXBean annotation not detected correctly", JmxUtils.isMBean(FooNotX.class));
	}

	@Test
	public void annotatedMXBean() throws Exception {
		assertTrue("MXBean annotation not detected correctly", JmxUtils.isMBean(FooX.class));
	}


	@MXBean(false)
	public interface FooNotMXBean {
		String getName();
	}

	public static class FooNotX implements FooNotMXBean {

		@Override
		public String getName() {
			return "Rob Harrop";
		}
	}

	@MXBean(true)
	public interface FooIfc {
		String getName();
	}

	public static class FooX implements FooIfc {

		@Override
		public String getName() {
			return "Rob Harrop";
		}
	}

}
