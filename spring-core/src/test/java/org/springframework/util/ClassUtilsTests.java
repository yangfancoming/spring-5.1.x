

package org.springframework.util;

import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import org.springframework.tests.sample.objects.DerivedTestObject;
import org.springframework.tests.sample.objects.ITestInterface;
import org.springframework.tests.sample.objects.ITestObject;
import org.springframework.tests.sample.objects.TestObject;

import static org.junit.Assert.*;


public class ClassUtilsTests {

	private ClassLoader classLoader = getClass().getClassLoader();

	@Test
	public void resolvePrimitiveClassNameTest(){
		System.out.println(ClassUtils.resolvePrimitiveClassName("boolean"));
	}

	@Test
	public void testClassLoader(){
//		assertEquals("sun.misc.Launcher$AppClassLoader@14dad5dc",Thread.currentThread().getContextClassLoader());
		System.out.println(Thread.currentThread().getContextClassLoader());
		System.out.println(getClass().getClassLoader());
		System.out.println(ClassLoader.getSystemClassLoader());
		System.out.println(ClassLoader.getSystemClassLoader().getParent());
	}

	@Before
	public void clearStatics() {
		InnerClass.noArgCalled = false;
		InnerClass.argCalled = false;
		InnerClass.overloadedCalled = false;
	}

	@Test
	public void testIsPresent() {
		// 判断指定类 是否已加载到jvm
		assertTrue(ClassUtils.isPresent("java.lang.String", classLoader));
		assertFalse(ClassUtils.isPresent("java.lang.MySpecialString", classLoader));
		assertTrue(ClassUtils.isPresent("org.springframework.util.ClassUtilsTests", classLoader));
		// 判断内部类 是否已加载到jvm
		assertTrue(ClassUtils.isPresent("org.springframework.util.ClassUtilsTests$InnerClass", classLoader));
	}

	// 测试反射创建对象
	@Test
	public void testForName() throws ClassNotFoundException {
		assertEquals(String.class, ClassUtils.forName("java.lang.String", classLoader));

		assertEquals(String[].class, ClassUtils.forName("java.lang.String[]", classLoader));
		assertEquals(String[].class, ClassUtils.forName(String[].class.getName(), classLoader));

		assertEquals(String[][].class, ClassUtils.forName(String[][].class.getName(), classLoader));
		assertEquals(String[][][].class, ClassUtils.forName(String[][][].class.getName(), classLoader));
		assertEquals(TestObject.class, ClassUtils.forName("org.springframework.tests.sample.objects.TestObject", classLoader));

		assertEquals(TestObject[].class, ClassUtils.forName("org.springframework.tests.sample.objects.TestObject[]", classLoader));
		assertEquals(TestObject[].class, ClassUtils.forName(TestObject[].class.getName(), classLoader));

		assertEquals(TestObject[][].class, ClassUtils.forName("org.springframework.tests.sample.objects.TestObject[][]", classLoader));
		assertEquals(TestObject[][].class, ClassUtils.forName(TestObject[][].class.getName(), classLoader));

		// 测试 ClassUtils.forName 创建内部的情况
		assertEquals(InnerClass.class, ClassUtils.forName(InnerClass.class.getName(), classLoader));
		assertEquals(InnerClass.class, ClassUtils.forName("org.springframework.util.ClassUtilsTests$InnerClass", classLoader));

		assertEquals(short[][][].class, ClassUtils.forName("[[[S", classLoader));
	}

	// 测试 创建java 8中原生类型
	@Test
	public void testForNameWithPrimitiveClasses() throws ClassNotFoundException {
		assertEquals(boolean.class, ClassUtils.forName("boolean", classLoader));
		assertEquals(byte.class, ClassUtils.forName("byte", classLoader));
		assertEquals(char.class, ClassUtils.forName("char", classLoader));
		assertEquals(short.class, ClassUtils.forName("short", classLoader));
		assertEquals(int.class, ClassUtils.forName("int", classLoader));
		assertEquals(long.class, ClassUtils.forName("long", classLoader));
		assertEquals(float.class, ClassUtils.forName("float", classLoader));
		assertEquals(double.class, ClassUtils.forName("double", classLoader));
		assertEquals(void.class, ClassUtils.forName("void", classLoader));
	}

	// 测试 创建java 8中原生类型数组
	@Test
	public void testForNameWithPrimitiveArrays() throws ClassNotFoundException {
		assertEquals(boolean[].class, ClassUtils.forName("boolean[]", classLoader));
		assertEquals(byte[].class, ClassUtils.forName("byte[]", classLoader));
		assertEquals(char[].class, ClassUtils.forName("char[]", classLoader));
		assertEquals(short[].class, ClassUtils.forName("short[]", classLoader));
		assertEquals(int[].class, ClassUtils.forName("int[]", classLoader));
		assertEquals(long[].class, ClassUtils.forName("long[]", classLoader));
		assertEquals(float[].class, ClassUtils.forName("float[]", classLoader));
		assertEquals(double[].class, ClassUtils.forName("double[]", classLoader));
	}

	@Test
	public void testForNameWithPrimitiveArraysInternalName() throws ClassNotFoundException {
		assertEquals(boolean[].class, ClassUtils.forName(boolean[].class.getName(), classLoader));
		assertEquals(byte[].class, ClassUtils.forName(byte[].class.getName(), classLoader));
		assertEquals(char[].class, ClassUtils.forName(char[].class.getName(), classLoader));
		assertEquals(short[].class, ClassUtils.forName(short[].class.getName(), classLoader));
		assertEquals(int[].class, ClassUtils.forName(int[].class.getName(), classLoader));
		assertEquals(long[].class, ClassUtils.forName(long[].class.getName(), classLoader));
		assertEquals(float[].class, ClassUtils.forName(float[].class.getName(), classLoader));
		assertEquals(double[].class, ClassUtils.forName(double[].class.getName(), classLoader));
	}

	@Test
	public void testIsCacheSafe() {
		ClassLoader childLoader1 = new ClassLoader(classLoader) {};
		ClassLoader childLoader2 = new ClassLoader(classLoader) {};
		ClassLoader childLoader3 = new ClassLoader(classLoader) {
			@Override
			public Class<?> loadClass(String name) throws ClassNotFoundException {
				return childLoader1.loadClass(name);
			}
		};

		Class<?> composite = ClassUtils.createCompositeInterface(new Class<?>[] {Serializable.class, Externalizable.class}, childLoader1);

		assertTrue(ClassUtils.isCacheSafe(String.class, null));
		assertTrue(ClassUtils.isCacheSafe(String.class, classLoader));
		assertTrue(ClassUtils.isCacheSafe(String.class, childLoader1));
		assertTrue(ClassUtils.isCacheSafe(String.class, childLoader2));
		assertTrue(ClassUtils.isCacheSafe(String.class, childLoader3));

		assertFalse(ClassUtils.isCacheSafe(InnerClass.class, null));
		assertTrue(ClassUtils.isCacheSafe(InnerClass.class, classLoader));
		assertTrue(ClassUtils.isCacheSafe(InnerClass.class, childLoader1));
		assertTrue(ClassUtils.isCacheSafe(InnerClass.class, childLoader2));
		assertTrue(ClassUtils.isCacheSafe(InnerClass.class, childLoader3));
		assertFalse(ClassUtils.isCacheSafe(composite, null));
		assertFalse(ClassUtils.isCacheSafe(composite, classLoader));
		assertTrue(ClassUtils.isCacheSafe(composite, childLoader1));
		assertFalse(ClassUtils.isCacheSafe(composite, childLoader2));
		assertTrue(ClassUtils.isCacheSafe(composite, childLoader3));
	}

	private String errorClassName = "Class name did not match";

	// 测试 获取简单类名( 非全限定类名)
	@Test
	public void testGetShortName() {
		Class<? extends ClassUtilsTests> aClass = getClass(); // org.springframework.util.ClassUtilsTests
		assertEquals(errorClassName, "ClassUtilsTests", ClassUtils.getShortName(aClass));
	}

	// 测试 获取对象数组的 简单类名
	@Test
	public void testGetShortNameForObjectArrayClass() {
		String className = ClassUtils.getShortName(Object[].class);
		assertEquals(errorClassName, "Object[]", className);
	}

	// 测试 获取多维对象数组的 简单类名
	@Test
	public void testGetShortNameForMultiDimensionalObjectArrayClass() {
		String className = ClassUtils.getShortName(Object[][].class);
		assertEquals(errorClassName, "Object[][]", className);
	}

	// 测试 获取原始类型对象数组的 简单类名
	@Test
	public void testGetShortNameForPrimitiveArrayClass() {
		String className = ClassUtils.getShortName(byte[].class);
		assertEquals(errorClassName, "byte[]", className);
	}

	// 测试 获取多维原始类型对象数组的 简单类名
	@Test
	public void testGetShortNameForMultiDimensionalPrimitiveArrayClass() {
		String className = ClassUtils.getShortName(byte[][][].class);
		assertEquals(errorClassName, "byte[][][]", className);
	}

	// 测试 获取内部类的简单类名
	@Test
	public void testGetShortNameForInnerClass() {
		String className = ClassUtils.getShortName(InnerClass.class);
		assertEquals(errorClassName, "ClassUtilsTests.InnerClass", className);
	}

	@Test
	public void testGetShortNameAsProperty() { // org.springframework.util.ClassUtilsTests
		assertEquals(errorClassName, "classUtilsTests", ClassUtils.getShortNameAsProperty(this.getClass()));
		assertEquals(errorClassName, "ClassUtilsTests", ClassUtils.getShortName(getClass()));
	}

	// 测试获取类文件的名称
	@Test
	public void testGetClassFileName() {
		assertEquals("String.class", ClassUtils.getClassFileName(String.class));
		assertEquals("ClassUtilsTests.class", ClassUtils.getClassFileName(getClass()));
		assertEquals("ClassUtilsTests$InnerClass.class", ClassUtils.getClassFileName(InnerClass.class));
	}

	// 测试 获取类所在的包名
	@Test
	public void testGetPackageName() {
		assertEquals("java.lang", ClassUtils.getPackageName(String.class));
		assertEquals(getClass().getPackage().getName(), ClassUtils.getPackageName(getClass()));
		assertEquals("org.springframework.util", ClassUtils.getPackageName(ClassUtilsTests.class));
		assertEquals("org.springframework.util", ClassUtils.getPackageName(InnerClass.class));
	}

	// 测试 根据类得到类的权限定名；这个方法主要特点在于可以正确处理数组的类名
	@Test
	public void testGetQualifiedName() {
		assertEquals(errorClassName, "org.springframework.util.ClassUtilsTests", ClassUtils.getQualifiedName(getClass()));
		assertEquals(errorClassName, "java.lang.String", ClassUtils.getQualifiedName(String.class));
		assertEquals(errorClassName, "java.lang.String[]", ClassUtils.getQualifiedName(String[].class));
	}

	@Test
	public void testGetQualifiedNameForObjectArrayClass() {
		assertEquals(errorClassName, "java.lang.Object[]", ClassUtils.getQualifiedName(Object[].class));
	}

	@Test
	public void testGetQualifiedNameForMultiDimensionalObjectArrayClass() {
		assertEquals(errorClassName, "java.lang.Object[][]", ClassUtils.getQualifiedName(Object[][].class));
	}

	@Test
	public void testGetQualifiedNameForPrimitiveArrayClass() {
		assertEquals(errorClassName, "byte[]", ClassUtils.getQualifiedName(byte[].class));
	}

	@Test
	public void testGetQualifiedNameForMultiDimensionalPrimitiveArrayClass() {
		assertEquals(errorClassName, "byte[][]",  ClassUtils.getQualifiedName(byte[][].class));
	}

	// 判断给定类 是否有给定的公有方法
	@Test
	public void testHasMethod() {
		assertTrue(ClassUtils.hasMethod(Collection.class, "size"));
		assertTrue(ClassUtils.hasMethod(Collection.class, "remove", Object.class));
		assertFalse(ClassUtils.hasMethod(Collection.class, "remove"));
		assertFalse(ClassUtils.hasMethod(Collection.class, "someOtherMethod"));

		assertTrue(ClassUtils.hasMethod(InnerClass.class, "testPublic"));
		assertFalse(ClassUtils.hasMethod(InnerClass.class, "testPrivate"));
	}

	// 测试 给定类的给定方法 是否有效(无效则返回null)
	@Test
	public void testGetMethodIfAvailable() {
		Method method = ClassUtils.getMethodIfAvailable(Collection.class, "size");
		assertNotNull(method);
		assertEquals("size", method.getName());

		method = ClassUtils.getMethodIfAvailable(Collection.class, "remove", Object.class);
		assertNotNull(method);
		assertEquals("remove", method.getName());

		assertNull(ClassUtils.getMethodIfAvailable(Collection.class, "remove"));
		assertNull(ClassUtils.getMethodIfAvailable(Collection.class, "someOtherMethod"));
	}

	// 测试  通过方法名获取方法个数
	@Test
	public void testGetMethodCountForName() {
		assertEquals( 2,ClassUtils.getMethodCountForName(OverloadedMethodsClass.class, "print"));
		assertEquals( 5,ClassUtils.getMethodCountForName(SubOverloadedMethodsClass.class, "print"));
	}

	// 测试 给定的方法名 是否至少有一个方法存在
	@Test
	public void testCountOverloadedMethods() {
		assertFalse(ClassUtils.hasAtLeastOneMethodWithName(TestObject.class, "foobar"));
		// no args
		assertTrue(ClassUtils.hasAtLeastOneMethodWithName(TestObject.class, "hashCode"));
		// matches although it takes an arg
		assertTrue(ClassUtils.hasAtLeastOneMethodWithName(TestObject.class, "setAge"));
	}

	// 测试 反射调用 无参静态方法
	@Test
	public void testNoArgsStaticMethod() throws IllegalAccessException, InvocationTargetException {
		Method method = ClassUtils.getStaticMethod(InnerClass.class, "staticMethod");
		method.invoke(null, (Object[]) null);
		assertTrue("no argument method was not invoked.",InnerClass.noArgCalled);
	}

	// 测试 反射调用 有参静态方法
	@Test
	public void testArgsStaticMethod() throws IllegalAccessException, InvocationTargetException {
		Method method = ClassUtils.getStaticMethod(InnerClass.class, "argStaticMethod", String.class);
		method.invoke(null, "test");
		assertTrue("argument method was not invoked.", InnerClass.argCalled);
	}

	// 测试 反射调用 有参静态方法 重载
	@Test
	public void testOverloadedStaticMethod() throws IllegalAccessException, InvocationTargetException {
		Method method = ClassUtils.getStaticMethod(InnerClass.class, "staticMethod", String.class);
		method.invoke(null, "test");
		assertTrue("argument method was not invoked.", InnerClass.overloadedCalled);
	}


	// 测试 右侧类是否为左侧类的子类
	@Test
	public void testIsAssignable() {
		assertTrue(ClassUtils.isAssignable(Object.class, Object.class));
		assertTrue(ClassUtils.isAssignable(String.class, String.class));
		assertTrue(ClassUtils.isAssignable(Object.class, String.class));
		assertTrue(ClassUtils.isAssignable(Object.class, Integer.class));
		assertTrue(ClassUtils.isAssignable(Number.class, Integer.class));
		assertTrue(ClassUtils.isAssignable(Number.class, int.class));
		assertTrue(ClassUtils.isAssignable(Integer.class, int.class));

		assertTrue(ClassUtils.isAssignable(int.class, Integer.class));
		assertFalse(ClassUtils.isAssignable(String.class, Object.class));
		assertFalse(ClassUtils.isAssignable(Integer.class, Number.class));
		assertFalse(ClassUtils.isAssignable(Integer.class, double.class));
		assertFalse(ClassUtils.isAssignable(double.class, Integer.class));
	}

	// 测试 给定类 返回其所在包路径  / 格式
	@Test
	public void testClassPackageAsResourcePath() {
		String result = ClassUtils.classPackageAsResourcePath(Proxy.class);
		assertEquals("java/lang/reflect", result);
	}

	// 测试 路径拼接
	@Test
	public void testAddResourcePathToPackagePath() {
		String result = "java/lang/reflect/xyzabc.xml";
		assertEquals(result, ClassUtils.addResourcePathToPackagePath(Proxy.class, "xyzabc.xml"));
		assertEquals(result, ClassUtils.addResourcePathToPackagePath(Proxy.class, "/xyzabc.xml"));
		assertEquals("java/lang/reflect/a/b/c/d.xml",ClassUtils.addResourcePathToPackagePath(Proxy.class, "a/b/c/d.xml"));
	}

	// 测试  返回给定对象 所实现的所有接口 (包括其父类实现的接口)
	@Test
	public void testGetAllInterfaces() {
		DerivedTestObject testBean = new DerivedTestObject();
		List<Class<?>> ifcs = Arrays.asList(ClassUtils.getAllInterfaces(testBean));
		assertEquals("Correct number of interfaces", 4, ifcs.size());
		assertTrue("Contains Serializable", ifcs.contains(Serializable.class));
		assertTrue("Contains ITestBean", ifcs.contains(ITestObject.class));
		assertTrue("Contains IOther", ifcs.contains(ITestInterface.class));
	}

	@Test
	public void testClassNamesToString() {
		List<Class<?>> ifcs = new LinkedList<>();
		ifcs.add(Serializable.class);
		ifcs.add(Runnable.class);
		assertEquals("[interface java.io.Serializable, interface java.lang.Runnable]", ifcs.toString());
		assertEquals("[java.io.Serializable, java.lang.Runnable]", ClassUtils.classNamesToString(ifcs));

		List<Class<?>> classes = new LinkedList<>();
		classes.add(LinkedList.class);
		classes.add(Integer.class);
		assertEquals("[class java.util.LinkedList, class java.lang.Integer]", classes.toString());
		assertEquals("[java.util.LinkedList, java.lang.Integer]", ClassUtils.classNamesToString(classes));

		assertEquals("[interface java.util.List]", Collections.singletonList(List.class).toString());
		assertEquals("[java.util.List]", ClassUtils.classNamesToString(List.class));

		assertEquals("[]", Collections.EMPTY_LIST.toString());
		assertEquals("[]", ClassUtils.classNamesToString(Collections.emptyList()));
	}

	// 测试  返回 输入两个类的中 层级高的那个类
	@Test
	public void testDetermineCommonAncestor() {
		assertEquals(Number.class, ClassUtils.determineCommonAncestor(Integer.class, Number.class));
		assertEquals(Number.class, ClassUtils.determineCommonAncestor(Number.class, Integer.class));
		assertEquals(Number.class, ClassUtils.determineCommonAncestor(Number.class, null));
		assertEquals(Integer.class, ClassUtils.determineCommonAncestor(null, Integer.class));
		assertEquals(Integer.class, ClassUtils.determineCommonAncestor(Integer.class, Integer.class));

		assertEquals(Number.class, ClassUtils.determineCommonAncestor(Integer.class, Float.class));
		assertEquals(Number.class, ClassUtils.determineCommonAncestor(Float.class, Integer.class));
		assertNull(ClassUtils.determineCommonAncestor(Integer.class, String.class));
		assertNull(ClassUtils.determineCommonAncestor(String.class, Integer.class));

		assertEquals(Collection.class, ClassUtils.determineCommonAncestor(List.class, Collection.class));
		assertEquals(Collection.class, ClassUtils.determineCommonAncestor(Collection.class, List.class));
		assertEquals(Collection.class, ClassUtils.determineCommonAncestor(Collection.class, null));
		assertEquals(List.class, ClassUtils.determineCommonAncestor(null, List.class));
		assertEquals(List.class, ClassUtils.determineCommonAncestor(List.class, List.class));

		assertNull(ClassUtils.determineCommonAncestor(List.class, Set.class));
		assertNull(ClassUtils.determineCommonAncestor(Set.class, List.class));
		assertNull(ClassUtils.determineCommonAncestor(List.class, Runnable.class));
		assertNull(ClassUtils.determineCommonAncestor(Runnable.class, List.class));

		assertEquals(List.class, ClassUtils.determineCommonAncestor(List.class, ArrayList.class));
		assertEquals(List.class, ClassUtils.determineCommonAncestor(ArrayList.class, List.class));
		assertNull(ClassUtils.determineCommonAncestor(List.class, String.class));
		assertNull(ClassUtils.determineCommonAncestor(String.class, List.class));
	}


	public static class InnerClass {
		static boolean noArgCalled;
		static boolean argCalled;
		static boolean overloadedCalled;

		public InnerClass() {
			System.out.println("InnerClass  is  invoked!");
		}

		public static void staticMethod() {
			System.out.println("noArgCalled  is  invoked!");
			noArgCalled = true;
		}
		public static void staticMethod(String anArg) {
			System.out.println("overloadedCalled  is  invoked!");
			overloadedCalled = true;
		}
		public static void argStaticMethod(String anArg) {
			System.out.println("argStaticMethod  is  invoked!");
			argCalled = true;
		}
		private void testPrivate(){}
		public void testPublic(){}
	}

	@SuppressWarnings("unused")
	private static class OverloadedMethodsClass {
		public void print(String messages) {}
		public void print(String[] messages) {}
	}

	@SuppressWarnings("unused")
	private static class SubOverloadedMethodsClass extends OverloadedMethodsClass {
		public void print(String messages) {}
		public void print(String header, String[] messages) {}
		void print(String header, String[] messages, String footer) {}
	}
}
