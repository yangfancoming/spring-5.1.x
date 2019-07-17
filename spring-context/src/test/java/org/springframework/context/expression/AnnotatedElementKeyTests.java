

package org.springframework.context.expression;

import java.lang.reflect.Method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.springframework.util.ReflectionUtils;

import static org.junit.Assert.*;


public class AnnotatedElementKeyTests {

	@Rule
	public final TestName name = new TestName();

	@Test
	public void sameInstanceEquals() {
		Method m = ReflectionUtils.findMethod(getClass(), name.getMethodName());
		AnnotatedElementKey instance = new AnnotatedElementKey(m, getClass());
		assertKeyEquals(instance, instance);
	}

	@Test
	public void equals() {
		Method m = ReflectionUtils.findMethod(getClass(), name.getMethodName());
		AnnotatedElementKey first = new AnnotatedElementKey(m, getClass());
		AnnotatedElementKey second = new AnnotatedElementKey(m, getClass());

		assertKeyEquals(first, second);
	}

	@Test
	public void equalsNoTarget() {
		Method m = ReflectionUtils.findMethod(getClass(), name.getMethodName());
		AnnotatedElementKey first = new AnnotatedElementKey(m, null);
		AnnotatedElementKey second = new AnnotatedElementKey(m, null);

		assertKeyEquals(first, second);
	}

	@Test
	public void noTargetClassNotEquals() {
		Method m = ReflectionUtils.findMethod(getClass(), name.getMethodName());
		AnnotatedElementKey first = new AnnotatedElementKey(m, getClass());
		AnnotatedElementKey second = new AnnotatedElementKey(m, null);

		assertFalse(first.equals(second));
	}

	protected void assertKeyEquals(AnnotatedElementKey first, AnnotatedElementKey second) {
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}

}
