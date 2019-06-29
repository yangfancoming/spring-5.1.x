

package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.tests.sample.objects.TestObject;

import static org.junit.Assert.*;

public class PrioritizedParameterNameDiscovererTests {

	private static final String[] FOO_BAR = new String[] { "foo", "bar" };

	private static final String[] SOMETHING_ELSE = new String[] { "something", "else" };

	private final ParameterNameDiscoverer returnsFooBar = new ParameterNameDiscoverer() {
		@Override
		public String[] getParameterNames(Method m) {
			return FOO_BAR;
		}
		@Override
		public String[] getParameterNames(Constructor<?> ctor) {
			return FOO_BAR;
		}
	};

	private final ParameterNameDiscoverer returnsSomethingElse = new ParameterNameDiscoverer() {
		@Override
		public String[] getParameterNames(Method m) {
			return SOMETHING_ELSE;
		}
		@Override
		public String[] getParameterNames(Constructor<?> ctor) {
			return SOMETHING_ELSE;
		}
	};

	private final Method anyMethod;

	public PrioritizedParameterNameDiscovererTests() throws SecurityException, NoSuchMethodException {
		anyMethod = TestObject.class.getMethod("getAge");
	}

	@Test
	public void noParametersDiscoverers() {
		ParameterNameDiscoverer pnd = new PrioritizedParameterNameDiscoverer();
		assertNull(pnd.getParameterNames(anyMethod));
		assertNull(pnd.getParameterNames((Constructor<?>) null));
	}

	@Test
	public void orderedParameterDiscoverers1() {
		PrioritizedParameterNameDiscoverer pnd = new PrioritizedParameterNameDiscoverer();
		pnd.addDiscoverer(returnsFooBar);
		assertTrue(Arrays.equals(FOO_BAR, pnd.getParameterNames(anyMethod)));
		assertTrue(Arrays.equals(FOO_BAR, pnd.getParameterNames((Constructor<?>) null)));
		pnd.addDiscoverer(returnsSomethingElse);
		assertTrue(Arrays.equals(FOO_BAR, pnd.getParameterNames(anyMethod)));
		assertTrue(Arrays.equals(FOO_BAR, pnd.getParameterNames((Constructor<?>) null)));
	}

	@Test
	public void orderedParameterDiscoverers2() {
		PrioritizedParameterNameDiscoverer pnd = new PrioritizedParameterNameDiscoverer();
		pnd.addDiscoverer(returnsSomethingElse);
		assertTrue(Arrays.equals(SOMETHING_ELSE, pnd.getParameterNames(anyMethod)));
		assertTrue(Arrays.equals(SOMETHING_ELSE, pnd.getParameterNames((Constructor<?>) null)));
		pnd.addDiscoverer(returnsFooBar);
		assertTrue(Arrays.equals(SOMETHING_ELSE, pnd.getParameterNames(anyMethod)));
		assertTrue(Arrays.equals(SOMETHING_ELSE, pnd.getParameterNames((Constructor<?>) null)));
	}

}
