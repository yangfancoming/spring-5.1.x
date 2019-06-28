

package org.springframework.aop.support;

import java.lang.reflect.Method;

import org.junit.Test;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.lang.Nullable;
import org.springframework.tests.aop.interceptor.NopInterceptor;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.SerializationTestUtils;

import static org.junit.Assert.*;

/**
 * @author Rod Johnson
 * @author Chris Beams
 */
public class AopUtilsTests {

	@Test
	public void testPointcutCanNeverApply() {
		class TestPointcut extends StaticMethodMatcherPointcut {
			@Override
			public boolean matches(Method method, @Nullable Class<?> clazzy) {
				return false;
			}
		}

		Pointcut no = new TestPointcut();
		assertFalse(AopUtils.canApply(no, Object.class));
	}

	@Test
	public void testPointcutAlwaysApplies() {
		assertTrue(AopUtils.canApply(new DefaultPointcutAdvisor(new NopInterceptor()), Object.class));
		assertTrue(AopUtils.canApply(new DefaultPointcutAdvisor(new NopInterceptor()), TestBean.class));
	}

	@Test
	public void testPointcutAppliesToOneMethodOnObject() {
		class TestPointcut extends StaticMethodMatcherPointcut {
			@Override
			public boolean matches(Method method, @Nullable Class<?> clazz) {
				return method.getName().equals("hashCode");
			}
		}

		Pointcut pc = new TestPointcut();

		// will return true if we're not proxying interfaces
		assertTrue(AopUtils.canApply(pc, Object.class));
	}

	/**
	 * Test that when we serialize and deserialize various canonical instances
	 * of AOP classes, they return the same instance, not a new instance
	 * that's subverted the singleton construction limitation.
	 */
	@Test
	public void testCanonicalFrameworkClassesStillCanonicalOnDeserialization() throws Exception {
		assertSame(MethodMatcher.TRUE, SerializationTestUtils.serializeAndDeserialize(MethodMatcher.TRUE));
		assertSame(ClassFilter.TRUE, SerializationTestUtils.serializeAndDeserialize(ClassFilter.TRUE));
		assertSame(Pointcut.TRUE, SerializationTestUtils.serializeAndDeserialize(Pointcut.TRUE));
		assertSame(EmptyTargetSource.INSTANCE, SerializationTestUtils.serializeAndDeserialize(EmptyTargetSource.INSTANCE));
		assertSame(Pointcuts.SETTERS, SerializationTestUtils.serializeAndDeserialize(Pointcuts.SETTERS));
		assertSame(Pointcuts.GETTERS, SerializationTestUtils.serializeAndDeserialize(Pointcuts.GETTERS));
		assertSame(ExposeInvocationInterceptor.INSTANCE,
				SerializationTestUtils.serializeAndDeserialize(ExposeInvocationInterceptor.INSTANCE));
	}

}
