

package org.springframework.aop.support;

import java.lang.reflect.Method;

import org.junit.Test;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;
import org.springframework.tests.sample.beans.TestBean;

import static org.junit.Assert.*;


public class ComposablePointcutTests {

	public static MethodMatcher GETTER_METHOD_MATCHER = new StaticMethodMatcher() {
		@Override
		public boolean matches(Method m, @Nullable Class<?> targetClass) {
			return m.getName().startsWith("get");
		}
	};

	public static MethodMatcher GET_AGE_METHOD_MATCHER = new StaticMethodMatcher() {
		@Override
		public boolean matches(Method m, @Nullable Class<?> targetClass) {
			return m.getName().equals("getAge");
		}
	};

	public static MethodMatcher ABSQUATULATE_METHOD_MATCHER = new StaticMethodMatcher() {
		@Override
		public boolean matches(Method m, @Nullable Class<?> targetClass) {
			return m.getName().equals("absquatulate");
		}
	};

	public static MethodMatcher SETTER_METHOD_MATCHER = new StaticMethodMatcher() {
		@Override
		public boolean matches(Method m, @Nullable Class<?> targetClass) {
			return m.getName().startsWith("set");
		}
	};


	@Test
	public void testMatchAll() throws NoSuchMethodException {
		Pointcut pc = new ComposablePointcut();
		assertTrue(pc.getClassFilter().matches(Object.class));
		assertTrue(pc.getMethodMatcher().matches(Object.class.getMethod("hashCode"), Exception.class));
	}

	@Test
	public void testFilterByClass() throws NoSuchMethodException {
		ComposablePointcut pc = new ComposablePointcut();

		assertTrue(pc.getClassFilter().matches(Object.class));

		ClassFilter cf = new RootClassFilter(Exception.class);
		pc.intersection(cf);
		assertFalse(pc.getClassFilter().matches(Object.class));
		assertTrue(pc.getClassFilter().matches(Exception.class));
		pc.intersection(new RootClassFilter(NestedRuntimeException.class));
		assertFalse(pc.getClassFilter().matches(Exception.class));
		assertTrue(pc.getClassFilter().matches(NestedRuntimeException.class));
		assertFalse(pc.getClassFilter().matches(String.class));
		pc.union(new RootClassFilter(String.class));
		assertFalse(pc.getClassFilter().matches(Exception.class));
		assertTrue(pc.getClassFilter().matches(String.class));
		assertTrue(pc.getClassFilter().matches(NestedRuntimeException.class));
	}

	@Test
	public void testUnionMethodMatcher() {
		// Matches the getAge() method in any class
		ComposablePointcut pc = new ComposablePointcut(ClassFilter.TRUE, GET_AGE_METHOD_MATCHER);
		assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
		assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
		assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));

		pc.union(GETTER_METHOD_MATCHER);
		// Should now match all getter methods
		assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
		assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
		assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));

		pc.union(ABSQUATULATE_METHOD_MATCHER);
		// Should now match absquatulate() as well
		assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
		assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
		assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
		// But it doesn't match everything
		assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class));
	}

	@Test
	public void testIntersectionMethodMatcher() {
		ComposablePointcut pc = new ComposablePointcut();
		assertTrue(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
		assertTrue(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
		assertTrue(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
		pc.intersection(GETTER_METHOD_MATCHER);
		assertFalse(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
		assertTrue(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
		assertTrue(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
		pc.intersection(GET_AGE_METHOD_MATCHER);
		// Use the Pointcuts matches method
		assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
		assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
		assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
	}

	@Test
	public void testEqualsAndHashCode() throws Exception {
		ComposablePointcut pc1 = new ComposablePointcut();
		ComposablePointcut pc2 = new ComposablePointcut();

		assertEquals(pc1, pc2);
		assertEquals(pc1.hashCode(), pc2.hashCode());

		pc1.intersection(GETTER_METHOD_MATCHER);

		assertFalse(pc1.equals(pc2));
		assertFalse(pc1.hashCode() == pc2.hashCode());

		pc2.intersection(GETTER_METHOD_MATCHER);

		assertEquals(pc1, pc2);
		assertEquals(pc1.hashCode(), pc2.hashCode());

		pc1.union(GET_AGE_METHOD_MATCHER);
		pc2.union(GET_AGE_METHOD_MATCHER);

		assertEquals(pc1, pc2);
		assertEquals(pc1.hashCode(), pc2.hashCode());
	}

}
