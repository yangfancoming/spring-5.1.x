

package org.springframework.core.type;

import java.lang.reflect.Method;

import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import static org.junit.Assert.*;

/**
 * @author Ramnivas Laddad
 * @author Sam Brannen
 */
abstract class ClassloadingAssertions {

	private static boolean isClassLoaded(String className) {
		ClassLoader cl = ClassUtils.getDefaultClassLoader();
		Method findLoadedClassMethod = ReflectionUtils.findMethod(cl.getClass(), "findLoadedClass", String.class);
		ReflectionUtils.makeAccessible(findLoadedClassMethod);
		Class<?> loadedClass = (Class<?>) ReflectionUtils.invokeMethod(findLoadedClassMethod, cl, className);
		return loadedClass != null;
	}

	public static void assertClassNotLoaded(String className) {
		assertFalse("Class [" + className + "] should not have been loaded", isClassLoaded(className));
	}

}
