

package org.springframework.aop.aspectj;

import org.junit.Test;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.tests.sample.beans.CountingTestBean;
import org.springframework.tests.sample.beans.IOther;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.tests.sample.beans.subpkg.DeepBean;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link TypePatternClassFilter} class.
 *
 * @author Rod Johnson
 * @author Rick Evans
 * @author Chris Beams
 */
public class TypePatternClassFilterTests {

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidPattern() {
		// should throw - pattern must be recognized as invalid
		new TypePatternClassFilter("-");
	}

	@Test
	public void testValidPatternMatching() {
		TypePatternClassFilter tpcf = new TypePatternClassFilter("org.springframework.tests.sample.beans.*");
		assertTrue("Must match: in package", tpcf.matches(TestBean.class));
		assertTrue("Must match: in package", tpcf.matches(ITestBean.class));
		assertTrue("Must match: in package", tpcf.matches(IOther.class));
		assertFalse("Must be excluded: in wrong package", tpcf.matches(DeepBean.class));
		assertFalse("Must be excluded: in wrong package", tpcf.matches(BeanFactory.class));
		assertFalse("Must be excluded: in wrong package", tpcf.matches(DefaultListableBeanFactory.class));
	}

	@Test
	public void testSubclassMatching() {
		TypePatternClassFilter tpcf = new TypePatternClassFilter("org.springframework.tests.sample.beans.ITestBean+");
		assertTrue("Must match: in package", tpcf.matches(TestBean.class));
		assertTrue("Must match: in package", tpcf.matches(ITestBean.class));
		assertTrue("Must match: in package", tpcf.matches(CountingTestBean.class));
		assertFalse("Must be excluded: not subclass", tpcf.matches(IOther.class));
		assertFalse("Must be excluded: not subclass", tpcf.matches(DefaultListableBeanFactory.class));
	}

	@Test
	public void testAndOrNotReplacement() {
		TypePatternClassFilter tpcf = new TypePatternClassFilter("java.lang.Object or java.lang.String");
		assertFalse("matches Number",tpcf.matches(Number.class));
		assertTrue("matches Object",tpcf.matches(Object.class));
		assertTrue("matchesString",tpcf.matches(String.class));
		tpcf = new TypePatternClassFilter("java.lang.Number+ and java.lang.Float");
		assertTrue("matches Float",tpcf.matches(Float.class));
		assertFalse("matches Double",tpcf.matches(Double.class));
		tpcf = new TypePatternClassFilter("java.lang.Number+ and not java.lang.Float");
		assertFalse("matches Float",tpcf.matches(Float.class));
		assertTrue("matches Double",tpcf.matches(Double.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetTypePatternWithNullArgument() throws Exception {
		new TypePatternClassFilter(null);
	}

	@Test(expected = IllegalStateException.class)
	public void testInvocationOfMatchesMethodBlowsUpWhenNoTypePatternHasBeenSet() throws Exception {
		new TypePatternClassFilter().matches(String.class);
	}

}
