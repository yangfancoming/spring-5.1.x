

package org.springframework.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class MethodParameterTests {

	private Method method;
	private MethodParameter stringParameter;
	private MethodParameter longParameter;
	private MethodParameter intReturnType;

	@Before
	public void setup() throws NoSuchMethodException {
		method = getClass().getMethod("method", String.class, Long.TYPE);
		stringParameter = new MethodParameter(method, 0);
		longParameter = new MethodParameter(method, 1);
		intReturnType = new MethodParameter(method, -1);
	}

	// 测试 Equals  只有parameterIndex相同 才会相同
	@Test
	public void testEquals() throws NoSuchMethodException {
		assertEquals(stringParameter, stringParameter);
		assertEquals(longParameter, longParameter);
		assertEquals(intReturnType, intReturnType);

		assertFalse(stringParameter.equals(longParameter));
		assertFalse(stringParameter.equals(intReturnType));
		assertFalse(longParameter.equals(stringParameter));
		assertFalse(longParameter.equals(intReturnType));
		assertFalse(intReturnType.equals(stringParameter));
		assertFalse(intReturnType.equals(longParameter));

		Method method = getClass().getMethod("method", String.class, Long.TYPE);
		MethodParameter methodParameter = new MethodParameter(method, 0);
		assertEquals(stringParameter, methodParameter);
		assertEquals(methodParameter, stringParameter);
		assertTrue(stringParameter.equals(methodParameter));

		assertNotEquals(longParameter, methodParameter);
		assertNotEquals(methodParameter, longParameter);
		assertFalse(methodParameter.equals(longParameter));
	}

	// 测试  HashCode 只有parameterIndex相同 才会相同
	@Test
	public void testHashCode() throws NoSuchMethodException {
		assertEquals(stringParameter.hashCode(), stringParameter.hashCode());
		assertEquals(longParameter.hashCode(), longParameter.hashCode());
		assertEquals(intReturnType.hashCode(), intReturnType.hashCode());

		Method method = getClass().getMethod("method", String.class, Long.TYPE);
		MethodParameter methodParameter = new MethodParameter(method, 0);
		assertEquals(stringParameter.hashCode(), methodParameter.hashCode());
		assertNotEquals(longParameter.hashCode(), methodParameter.hashCode());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testFactoryMethods() {
		assertEquals(stringParameter, MethodParameter.forMethodOrConstructor(method, 0));
		assertEquals(longParameter, MethodParameter.forMethodOrConstructor(method, 1));

		assertEquals(stringParameter, MethodParameter.forExecutable(method, 0));
		assertEquals(longParameter, MethodParameter.forExecutable(method, 1));

		assertEquals(stringParameter, MethodParameter.forParameter(method.getParameters()[0]));
		assertEquals(longParameter, MethodParameter.forParameter(method.getParameters()[1]));
	}

	// 测试 异常参数索引范围
	@Test(expected = IllegalArgumentException.class)
//	@Test
	public void testIndexValidation() {
		new MethodParameter(method, 2);
	}

	@Test
	public void annotatedConstructorParameterInStaticNestedClass() throws Exception {
		Constructor<?> constructor = NestedClass.class.getDeclaredConstructor(String.class);
		MethodParameter methodParameter = MethodParameter.forExecutable(constructor, 0);
		assertEquals(String.class, methodParameter.getParameterType());
		assertNotNull("Failed to find @Param annotation", methodParameter.getParameterAnnotation(Param.class));
	}

	@Test  // SPR-16652
	public void annotatedConstructorParameterInInnerClass() throws Exception {
		Constructor<?> constructor = InnerClass.class.getConstructor(getClass(), String.class, Callable.class);

		MethodParameter methodParameter = MethodParameter.forExecutable(constructor, 0);
		assertEquals(getClass(), methodParameter.getParameterType());
		assertNull(methodParameter.getParameterAnnotation(Param.class));

		methodParameter = MethodParameter.forExecutable(constructor, 1);
		assertEquals(String.class, methodParameter.getParameterType());
		assertNotNull("Failed to find @Param annotation", methodParameter.getParameterAnnotation(Param.class));

		methodParameter = MethodParameter.forExecutable(constructor, 2);
		assertEquals(Callable.class, methodParameter.getParameterType());
		assertNull(methodParameter.getParameterAnnotation(Param.class));
	}

	@Test  // SPR-16734
	public void genericConstructorParameterInInnerClass() throws Exception {
		Constructor<?> constructor = InnerClass.class.getConstructor(getClass(), String.class, Callable.class);

		MethodParameter methodParameter = MethodParameter.forExecutable(constructor, 0);
		assertEquals(getClass(), methodParameter.getParameterType());
		assertEquals(getClass(), methodParameter.getGenericParameterType());

		methodParameter = MethodParameter.forExecutable(constructor, 1);
		assertEquals(String.class, methodParameter.getParameterType());
		assertEquals(String.class, methodParameter.getGenericParameterType());

		methodParameter = MethodParameter.forExecutable(constructor, 2);
		assertEquals(Callable.class, methodParameter.getParameterType());
		assertEquals(ResolvableType.forClassWithGenerics(Callable.class, Integer.class).getType(),methodParameter.getGenericParameterType());
	}

	public int method(String p1, long p2) {
		return 42;
	}

	@SuppressWarnings("unused")
	private static class NestedClass {
		NestedClass(@Param String s) {
		}
	}

	@SuppressWarnings("unused")
	private class InnerClass {
		public InnerClass(@Param String s, Callable<Integer> i) {
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.PARAMETER)
	private @interface Param {
	}
}
