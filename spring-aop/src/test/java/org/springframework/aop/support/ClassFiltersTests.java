

package org.springframework.aop.support;

import org.junit.Test;

import org.springframework.aop.ClassFilter;
import org.springframework.core.NestedRuntimeException;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;

/**
 * @author Rod Johnson
 * @author Chris Beams
 */
public class ClassFiltersTests {

	private ClassFilter exceptionFilter = new RootClassFilter(Exception.class);

	private ClassFilter itbFilter = new RootClassFilter(ITestBean.class);

	private ClassFilter hasRootCauseFilter = new RootClassFilter(NestedRuntimeException.class);

	@Test
	public void testUnion() {
		assertTrue(exceptionFilter.matches(RuntimeException.class));
		assertFalse(exceptionFilter.matches(TestBean.class));
		assertFalse(itbFilter.matches(Exception.class));
		assertTrue(itbFilter.matches(TestBean.class));
		ClassFilter union = ClassFilters.union(exceptionFilter, itbFilter);
		assertTrue(union.matches(RuntimeException.class));
		assertTrue(union.matches(TestBean.class));
	}

	@Test
	public void testIntersection() {
		assertTrue(exceptionFilter.matches(RuntimeException.class));
		assertTrue(hasRootCauseFilter.matches(NestedRuntimeException.class));
		ClassFilter intersection = ClassFilters.intersection(exceptionFilter, hasRootCauseFilter);
		assertFalse(intersection.matches(RuntimeException.class));
		assertFalse(intersection.matches(TestBean.class));
		assertTrue(intersection.matches(NestedRuntimeException.class));
	}

}
