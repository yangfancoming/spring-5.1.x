package org.springframework.util;
import java.beans.Introspector;
import java.io.Closeable;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.lang.Nullable;

/**
 * Miscellaneous {@code java.lang.Class} utility methods.
 * Mainly for internal use within the framework.
 * @since 1.1
 * @see TypeUtils
 * @see ReflectionUtils
 */
public abstract class ClassUtils {

	/** Suffix for array class names: {@code "[]"}. */
	public static final String ARRAY_SUFFIX = "[]";

	/** Prefix for internal array class names: {@code "["}. */
	private static final String INTERNAL_ARRAY_PREFIX = "[";

	/** Prefix for internal non-primitive array class names: {@code "[L"}. */
	private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

	/** The package separator character: {@code '.'}. */
	private static final char PACKAGE_SEPARATOR = '.';

	/** The path separator character: {@code '/'}. */
	private static final char PATH_SEPARATOR = '/';

	/** The inner class separator character: {@code '$'}. */
	private static final char INNER_CLASS_SEPARATOR = '$';

	/** The CGLIB class separator: {@code "$$"}. */
	public static final String CGLIB_CLASS_SEPARATOR = "$$";

	/** The ".class" file suffix. */
	public static final String CLASS_FILE_SUFFIX = ".class";

	/**
	 * Map with primitive wrapper type as key and corresponding primitive type as value, for example: Integer.class -> int.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

	/**
	 * Map with primitive type as key and corresponding wrapper type as value, for example: int.class -> Integer.class.
	 */
	private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);

	/**
	 * Map with primitive type name as key and corresponding primitive type as value, for example: "int" -> "int.class".
	 */
	private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);

	/**
	 * Map with common Java language class name as key and corresponding Class as value.
	 * Primarily for efficient deserialization of remote invocations.
	 */
	private static final Map<String, Class<?>> commonClassCache = new HashMap<>(64);

	/**
	 * Common Java language interfaces which are supposed to be ignored when searching for 'primary' user-level interfaces.
	 */
	private static final Set<Class<?>> javaLanguageInterfaces;

	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);

		// Map entry iteration is less expensive to initialize than forEach with lambdas
		for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
			primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
			registerCommonClasses(entry.getKey());
		}
		Set<Class<?>> primitiveTypes = new HashSet<>(32);
		primitiveTypes.addAll(primitiveWrapperTypeMap.values());
		Collections.addAll(primitiveTypes, boolean[].class, byte[].class, char[].class,double[].class, float[].class, int[].class, long[].class, short[].class);
		primitiveTypes.add(void.class);
		for (Class<?> primitiveType : primitiveTypes) {
			primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
		}
		registerCommonClasses(Boolean[].class, Byte[].class, Character[].class, Double[].class,Float[].class, Integer[].class, Long[].class, Short[].class);
		registerCommonClasses(Number.class, Number[].class, String.class, String[].class,Class.class, Class[].class, Object.class, Object[].class);
		registerCommonClasses(Throwable.class, Exception.class, RuntimeException.class,Error.class, StackTraceElement.class, StackTraceElement[].class);
		registerCommonClasses(Enum.class, Iterable.class, Iterator.class, Enumeration.class,Collection.class, List.class, Set.class, Map.class, Map.Entry.class, Optional.class);
		Class<?>[] javaLanguageInterfaceArray = {Serializable.class, Externalizable.class,Closeable.class, AutoCloseable.class, Cloneable.class, Comparable.class};
		registerCommonClasses(javaLanguageInterfaceArray);
		javaLanguageInterfaces = new HashSet<>(Arrays.asList(javaLanguageInterfaceArray));
	}

	/**
	 * Register the given common classes with the ClassUtils cache.
	 */
	private static void registerCommonClasses(Class<?>... commonClasses) {
		for (Class<?> clazz : commonClasses) {
			commonClassCache.put(clazz.getName(), clazz);
		}
	}

	/**
	 * 该方法用于获取默认的类加载器 （按照获取当前线程上下文类加载器-->获取当前类类加载器-->获取系统启动类加载器的顺序来获取）
	 * Return the default ClassLoader to use: typically the thread context ClassLoader,if available; 
	 * the ClassLoader that loaded the ClassUtils class will be used as fallback.
	 * Call this method if you intend to use the thread context ClassLoader n a scenario where you clearly prefer a non-null ClassLoader reference:
	 * for example, for class path resource loading (but not necessarily for {@code Class.forName},
	 * which accepts a {@code null} ClassLoader reference as well).
	 * @return the default ClassLoader (only {@code null} if even the system ClassLoader isn't accessible)
	 * @see Thread#getContextClassLoader()
	 * @see ClassLoader#getSystemClassLoader()
	 */
	@Nullable
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			//优先获取当前线程上下文类加载器
			cl = Thread.currentThread().getContextClassLoader();
		}catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			// 如果没有context loader，使用当前类的类加载器；
			cl = ClassUtils.class.getClassLoader();
			if (cl == null) {
				// getClassLoader() returning null indicates the bootstrap ClassLoader
				// 如果当前类加载器无法获取，获得bootstrap ClassLoader
				try {
					//获取SystemClassLoader
					cl = ClassLoader.getSystemClassLoader();
				}catch (Throwable ex) {
					// Cannot access system ClassLoader - oh well, maybe the caller can live with null...
				}
			}
		}
		return cl;
	}

	/**
	 * 这个方法较为简单，使用传入的classloader替换线程的classloader；
	 * 使用场景，比如一个线程的classloader和spring的classloader不一致的时候，就可以使用这个方法替换；
	 * Override the thread context ClassLoader with the environment's bean ClassLoader if necessary,
	 * i.e. if the bean ClassLoader is not equivalent to the thread context ClassLoader already.
	 * @param classLoaderToUse the actual ClassLoader to use for the thread context
	 * @return the original thread context ClassLoader, or {@code null} if not overridden
	 */
	@Nullable
	public static ClassLoader overrideThreadContextClassLoader(@Nullable ClassLoader classLoaderToUse) {
		Thread currentThread = Thread.currentThread();
		ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
		if (classLoaderToUse != null && !classLoaderToUse.equals(threadContextClassLoader)) {
			currentThread.setContextClassLoader(classLoaderToUse);
			return threadContextClassLoader;
		}else {
			return null;
		}
	}

	/**
	 * 通过反射获取类信息
	 * Replacement for {@code Class.forName()} that also returns Class instances  for primitives (e.g. "int") and array class names (e.g. "String[]").
	 * Furthermore, it is also capable of resolving inner class names in Java source style (e.g. "java.lang.Thread.State" instead of "java.lang.Thread$State").
	 * @param name the name of the Class
	 * @param classLoader the class loader to use (may be {@code null}, which indicates the default class loader)
	 * @return a class instance for the supplied name
	 * @throws ClassNotFoundException if the class was not found
	 * @throws LinkageError if the class file could not be loaded
	 * @see Class#forName(String, boolean, ClassLoader)
	 * 看名字就知道，是Class.forName的一个增强版本；通过指定的classloader加载对应的类；除了能正常加载普通的类型，还能加载简单类型，数组，或者内部类
	 */
	public static Class<?> forName(String name, @Nullable ClassLoader classLoader) throws ClassNotFoundException, LinkageError {
		Assert.notNull(name, "Name must not be null");
		Class<?> clazz = resolvePrimitiveClassName(name);
		if (clazz == null) clazz = commonClassCache.get(name);
		if (clazz != null) return clazz;
		// "java.lang.String[]" style arrays
		if (name.endsWith(ARRAY_SUFFIX)) {
			String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
			Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}
		// "[Ljava.lang.String;" style arrays
		if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
			String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
			Class<?> elementClass = forName(elementName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}
		// "[[I" or "[[Ljava.lang.String;" style arrays
		if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
			String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
			Class<?> elementClass = forName(elementName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}
		ClassLoader clToUse = classLoader;
		if (clToUse == null) {
			clToUse = getDefaultClassLoader();
		}
		try {
			return Class.forName(name, false, clToUse);
		}catch (ClassNotFoundException ex) {
			int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
			if (lastDotIndex != -1) {
				String innerClassName = name.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR + name.substring(lastDotIndex + 1);
				try {
					return Class.forName(innerClassName, false, clToUse);
				}catch (ClassNotFoundException ex2) {
					// Swallow - let original exception get through
				}
			}
			throw ex;
		}
	}

	/**
	 * 通过反射获取类信息
	 * Resolve the given class name into a Class instance. Supports primitives (like "int") and array class names (like "String[]").
	 * This is effectively equivalent to the {@code forName}  method with the same arguments,with the only difference being the exceptions thrown in case of class loading failure.
	 * @param className the name of the Class
	 * @param classLoader the class loader to use (may be {@code null}, which indicates the default class loader)
	 * @return a class instance for the supplied name
	 * @throws IllegalArgumentException if the class name was not resolvable (that is, the class could not be found or the class file could not be loaded)
	 * @throws IllegalStateException if the corresponding class is resolvable but there was a readability mismatch in the inheritance hierarchy of the class
	 * (typically a missing dependency declaration in a Jigsaw module definition
	 * for a superclass or interface implemented by the class to be loaded here)
	 * @see #forName(String, ClassLoader)
	 * 和forName方法相同，内部就是直接调用的forName方法，只是抛出的异常不一样而已；
	 */
	public static Class<?> resolveClassName(String className, @Nullable ClassLoader classLoader) throws IllegalArgumentException {
		try {
			return forName(className, classLoader);
		}catch (IllegalAccessError err) {
			throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(), err);
		}catch (LinkageError err) {
			throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", err);
		}catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Could not find class [" + className + "]", ex);
		}
	}

	/**
	 * Determine whether the {@link Class} identified by the supplied name is present and can be loaded.
	 * Will return {@code false} if either the class or one of its dependencies is not present or cannot be loaded.
	 * @param className the name of the class to check
	 * @param classLoader the class loader to use (may be {@code null} which indicates the default class loader)
	 * @return whether the specified class is present (including all of its superclasses and interfaces)
	 * @throws IllegalStateException if the corresponding class is resolvable but
	 * there was a readability mismatch in the inheritance hierarchy of the class
	 * (typically a missing dependency declaration in a Jigsaw module definition
	 * for a superclass or interface implemented by the class to be checked here)
	 */
	public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
		try {
			forName(className, classLoader);
			return true;
		}catch (IllegalAccessError err) {
			throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" + className + "]: " + err.getMessage(), err);
		}catch (Throwable ex) {
			// Typically ClassNotFoundException or NoClassDefFoundError...
			return false;
		}
	}

	/**
	 * 判断一个类型是否在指定类加载器中可见。
	 * Check whether the given class is visible in the given ClassLoader.
	 * @param clazz the class to check (typically an interface)
	 * @param classLoader the ClassLoader to check against
	 * (may be {@code null} in which case this method will always return {@code true})
	 */
	public static boolean isVisible(Class<?> clazz, @Nullable ClassLoader classLoader) {
		if (classLoader == null) return true;
		try {
			if (clazz.getClassLoader() == classLoader) {
				return true;
			}
		}catch (SecurityException ex) {
			// Fall through to loadable check below
		}
		// Visible if same Class can be loaded from given ClassLoader
		return isLoadable(clazz, classLoader);
	}

	/**
	 * 判断类是否是可以缓存的，原理很简单，就是判断该类型是否在指定classloader或者其parent classloader中；
	 * Check whether the given class is cache-safe in the given context,i.e. whether it is loaded by the given ClassLoader or a parent of it.
	 * @param clazz the class to analyze
	 * @param classLoader the ClassLoader to potentially cache metadata in (may be {@code null} which indicates the system class loader)
	 */
	public static boolean isCacheSafe(Class<?> clazz, @Nullable ClassLoader classLoader) {
		Assert.notNull(clazz, "Class must not be null");
		try {
			ClassLoader target = clazz.getClassLoader();
			// Common cases
			if (target == classLoader || target == null) return true;
			if (classLoader == null) return false;
			// Check for match in ancestors -> positive
			ClassLoader current = classLoader;
			while (current != null) {
				current = current.getParent();
				if (current == target) return true;
			}
			// Check for match in children -> negative
			while (target != null) {
				target = target.getParent();
				if (target == classLoader) return false;
			}
		}catch (SecurityException ex) {
			// Fall through to loadable check below
		}
		// Fallback for ClassLoaders without parent/child relationship:
		// safe if same Class can be loaded from given ClassLoader
		return (classLoader != null && isLoadable(clazz, classLoader));
	}

	/**
	 * Check whether the given class is loadable in the given ClassLoader.
	 * @param clazz the class to check (typically an interface)
	 * @param classLoader the ClassLoader to check against
	 * @since 5.0.6
	 */
	private static boolean isLoadable(Class<?> clazz, ClassLoader classLoader) {
		try {
			return (clazz == classLoader.loadClass(clazz.getName()));
			// Else: different class with same name found
		}catch (ClassNotFoundException ex) {
			// No corresponding class found at all
			return false;
		}
	}

	/**
	 * 获取简单类型的类；这个方法是用于处理forName方法中简单类型的调用方法；
	 * Resolve the given class name as primitive class, if appropriate, according to the JVM's naming rules for primitive classes.
	 * Also supports the JVM's internal class names for primitive arrays.
	 * Does <i>not</i> support the "[]" suffix notation for primitive arrays;
	 * this is only supported by {@link #forName(String, ClassLoader)}.
	 * @param name the name of the potentially primitive class
	 * @return the primitive class, or {@code null} if the name does not denote
	 * a primitive class or primitive array class
	 */
	@Nullable
	public static Class<?> resolvePrimitiveClassName(@Nullable String name) {
		Class<?> result = null;
		// Most class names will be quite long, considering that they
		// SHOULD sit in a package, so a length check is worthwhile.
		// 简单类型，最长值不要超过8，如果超过8，反而可以忽略了
		if (name != null && name.length() <= 8) {
			// Could be a primitive - likely.
			result = primitiveTypeNameMap.get(name);
		}
		return result;
	}

	/**
	 * 判定一个类是否是简单类型的包装类；
	 * Check if the given class represents a primitive wrapper,
	 * i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double.
	 * @param clazz the class to check
	 * @return whether the given class is a primitive wrapper class
	 */
	public static boolean isPrimitiveWrapper(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return primitiveWrapperTypeMap.containsKey(clazz);
	}

	/**
	 * 判定一个类是否是简单类型，或者是简单类型的包装类型；
	 * Check if the given class represents a primitive (i.e. boolean, byte,char, short, int, long, float, or double) or a primitive wrapper
	 * (i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double).
	 * @param clazz the class to check
	 * @return whether the given class is a primitive or primitive wrapper class
	 */
	public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
	}

	/**
	 * 判定一个类是否是简单类型的数组；
	 * Check if the given class represents an array of primitives,
	 * i.e. boolean, byte, char, short, int, long, float, or double.
	 * @param clazz the class to check
	 * @return whether the given class is a primitive array class
	 */
	public static boolean isPrimitiveArray(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isArray() && clazz.getComponentType().isPrimitive());
	}

	/**
	 * 判定一个类是否是简单类型的包装类型数组；
	 * Check if the given class represents an array of primitive wrappers,
	 * i.e. Boolean, Byte, Character, Short, Integer, Long, Float, or Double.
	 * @param clazz the class to check
	 * @return whether the given class is a primitive wrapper array class
	 */
	public static boolean isPrimitiveWrapperArray(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isArray() && isPrimitiveWrapper(clazz.getComponentType()));
	}

	/**
	 * 如果传入的类型是一个简单类型，返回这个简单类型的包装类型；
	 * Resolve the given class if it is a primitive class,returning the corresponding primitive wrapper type instead.
	 * @param clazz the class to check
	 * @return the original class, or a primitive wrapper for the original primitive type
	 */
	public static Class<?> resolvePrimitiveIfNecessary(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return (clazz.isPrimitive() && clazz != void.class ? primitiveTypeToWrapperMap.get(clazz) : clazz);
	}

	/**
	 * Check if the right-hand side type may be assigned to the left-hand side type, assuming setting by reflection.
	 * 右侧类是否为左侧类的子类
	 * Considers primitive wrapper classes as assignable to the corresponding primitive types.
	 * @param lhsType the target type
	 * @param rhsType the value type that should be assigned to the target type
	 * @return if the target type is assignable from the value type
	 * @see TypeUtils#isAssignable
	 */
	public static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
		Assert.notNull(lhsType, "Left-hand side type must not be null");
		Assert.notNull(rhsType, "Right-hand side type must not be null");
		if (lhsType.isAssignableFrom(rhsType)) return true;
		if (lhsType.isPrimitive()) {
			Class<?> resolvedPrimitive = primitiveWrapperTypeMap.get(rhsType);
			if (lhsType == resolvedPrimitive) return true;
		}else {
			Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
			if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine if the given type is assignable from the given value,
	 * assuming setting by reflection. Considers primitive wrapper classes  as assignable to the corresponding primitive types.
	 * @param type the target type
	 * @param value the value that should be assigned to the type
	 * @return if the type is assignable from the value
	 */
	public static boolean isAssignableValue(Class<?> type, @Nullable Object value) {
		Assert.notNull(type, "Type must not be null");
		return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
	}

	/**
	 * 把文件路径形式变成包路径形式；
	 * Convert a "/"-based resource path to a "."-based fully qualified class name.
	 * @param resourcePath the resource path pointing to a class
	 * @return the corresponding fully qualified class name
	 */
	public static String convertResourcePathToClassName(String resourcePath) {
		Assert.notNull(resourcePath, "Resource path must not be null");
		return resourcePath.replace(PATH_SEPARATOR, PACKAGE_SEPARATOR);
	}

	/**
	 * 把类路径形式变成文件路径形式；
	 * Convert a "."-based fully qualified class name to a "/"-based resource path.
	 * @param className the fully qualified class name
	 * @return the corresponding resource path, pointing to the class
	 */
	public static String convertClassNameToResourcePath(String className) {
		Assert.notNull(className, "Class name must not be null");
		return className.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
	}

	/**
	 * 在指定类的所属包下面，寻找一个资源文件，并返回该资源文件的文件路径；
	 * Return a path suitable for use with {@code ClassLoader.getResource}
	 * (also suitable for use with {@code Class.getResource} by prepending a slash ('/') to the return value).
	 *  Built by taking the package of the specified class file, converting all dots ('.') to slashes ('/'),
	 *  adding a trailing slash if necessary, and concatenating the specified resource name to this.
	 * <br/>As such, this function may be used to build a path suitable for loading a resource file that is in the same package as a class file,
	 * although {@link org.springframework.core.io.ClassPathResource} is usually even more convenient.
	 * @param clazz the Class whose package will be used as the base
	 * @param resourceName the resource name to append. A leading slash is optional.
	 * @return the built-up resource path
	 * @see ClassLoader#getResource
	 * @see Class#getResource
	 */
	public static String addResourcePathToPackagePath(Class<?> clazz, String resourceName) {
		Assert.notNull(resourceName, "Resource name must not be null");
		if (!resourceName.startsWith("/")) {
			return classPackageAsResourcePath(clazz) + '/' + resourceName;
		}
		return classPackageAsResourcePath(clazz) + resourceName;
	}

	/**
	 * 把指定类的包从包路径形式变为文件路径形式；
	 * Given an input class object, return a string which consists of the class's package name as a pathname,
	 *  i.e., all dots ('.') are replaced by slashes ('/').
	 *  Neither a leading nor trailing slash is added. ( 首尾没有 / 符号)
	 *  The result  could be concatenated with a slash and the name of a resource and fed directly to {@code ClassLoader.getResource()}.
	 *  For it to be fed to {@code Class.getResource} instead, a leading slash would also have to be prepended to the returned value.
	 * @param clazz the input class. A {@code null} value or the default
	 * (empty) package will result in an empty string ("") being returned.
	 * @return a path which represents the package name
	 * @see ClassLoader#getResource
	 * @see Class#getResource
	 */
	public static String classPackageAsResourcePath(@Nullable Class<?> clazz) {
		if (clazz == null) return "";
		String className = clazz.getName();
		int packageEndIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		if (packageEndIndex == -1) return "";
		String packageName = className.substring(0, packageEndIndex);
		return packageName.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
	}

	/**
	 * Build a String that consists of the names of the classes/interfaces in the given array.
	 * Basically like {@code AbstractCollection.toString()}, but stripping
	 * the "class "/"interface " prefix before every class name.
	 * @param classes an array of Class objects
	 * @return a String of form "[com.foo.Bar, com.foo.Baz]"
	 * @see java.util.AbstractCollection#toString()
	 */
	public static String classNamesToString(Class<?>... classes) {
		return classNamesToString(Arrays.asList(classes));
	}

	/**
	 * 把一组指定的类类名连成一个字符串；主要用于日志相关处理；
	 * Build a String that consists of the names of the classes/interfaces in the given collection.
	 * Basically like {@code AbstractCollection.toString()}, but stripping
	 * the "class "/"interface " prefix before every class name.
	 * @param classes a Collection of Class objects (may be {@code null})
	 * @return a String of form "[com.foo.Bar, com.foo.Baz]"
	 * @see java.util.AbstractCollection#toString()
	 */
	public static String classNamesToString(@Nullable Collection<Class<?>> classes) {
		if (CollectionUtils.isEmpty(classes)) {
			return "[]";
		}
		StringBuilder sb = new StringBuilder("[");
		for (Iterator<Class<?>> it = classes.iterator(); it.hasNext(); ) {
			Class<?> clazz = it.next();
			sb.append(clazz.getName());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * 将类集合变成类型数组；
	 * Copy the given {@code Collection} into a {@code Class} array.
	 * The {@code Collection} must contain {@code Class} elements only.
	 * @param collection the {@code Collection} to copy
	 * @return the {@code Class} array
	 * @since 3.1
	 * @see StringUtils#toStringArray
	 */
	public static Class<?>[] toClassArray(Collection<Class<?>> collection) {
		return collection.toArray(new Class<?>[0]);
	}

	/**
	 * 获取一个对象的所有接口；
	 * Return all interfaces that the given instance implements as an array,including ones implemented by superclasses.
	 * @param instance the instance to analyze for interfaces
	 * @return all interfaces that the given instance implements as an array
	 */
	public static Class<?>[] getAllInterfaces(Object instance) {
		Assert.notNull(instance, "Instance must not be null");
		return getAllInterfacesForClass(instance.getClass());
	}

	/**
	 * 获取一个类型的所有接口；
	 * Return all interfaces that the given class implements as an array,
	 * including ones implemented by superclasses.
	 * If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyze for interfaces
	 * @return all interfaces that the given object implements as an array
	 */
	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz) {
		return getAllInterfacesForClass(clazz, null);
	}

	/**
	 * 获取指定类加载器下的指定类型的所有接口；
	 * Return all interfaces that the given class implements as an array,
	 * including ones implemented by superclasses.
	 * If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyze for interfaces
	 * @param classLoader the ClassLoader that the interfaces need to be visible in
	 * (may be {@code null} when accepting all declared interfaces)
	 * @return all interfaces that the given object implements as an array
	 */
	public static Class<?>[] getAllInterfacesForClass(Class<?> clazz, @Nullable ClassLoader classLoader) {
		return toClassArray(getAllInterfacesForClassAsSet(clazz, classLoader));
	}

	/**
	 * 获取一个对象的所有接口，返回Set；
	 * Return all interfaces that the given instance implements as a Set,
	 * including ones implemented by superclasses.
	 * @param instance the instance to analyze for interfaces
	 * @return all interfaces that the given instance implements as a Set
	 */
	public static Set<Class<?>> getAllInterfacesAsSet(Object instance) {
		Assert.notNull(instance, "Instance must not be null");
		return getAllInterfacesForClassAsSet(instance.getClass());
	}

	/**
	 * 获取一个类的所有接口，返回Set；
	 * Return all interfaces that the given class implements as a Set,
	 * including ones implemented by superclasses.
	 * If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyze for interfaces
	 * @return all interfaces that the given object implements as a Set
	 */
	public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz) {
		return getAllInterfacesForClassAsSet(clazz, null);
	}

	/**
	 * 获取指定类加载器下的指定类型的所有接口；返回Set，这个方法才是上面5个方法的调用方法；
	 * Return all interfaces that the given class implements as a Set,
	 * including ones implemented by superclasses.
	 * If the class itself is an interface, it gets returned as sole interface.
	 * @param clazz the class to analyze for interfaces
	 * @param classLoader the ClassLoader that the interfaces need to be visible in
	 * (may be {@code null} when accepting all declared interfaces)
	 * @return all interfaces that the given object implements as a Set
	 */
	public static Set<Class<?>> getAllInterfacesForClassAsSet(Class<?> clazz, @Nullable ClassLoader classLoader) {
		Assert.notNull(clazz, "Class must not be null");
		//如果本身就是接口，返回自己
		if (clazz.isInterface() && isVisible(clazz, classLoader)) {
			return Collections.singleton(clazz);
		}
		Set<Class<?>> interfaces = new LinkedHashSet<>();
		Class<?> current = clazz;
		//使用递归获取类继承体系上所有接口；
		while (current != null) {
			Class<?>[] ifcs = current.getInterfaces();
			for (Class<?> ifc : ifcs) {
				if (isVisible(ifc, classLoader)) {
					interfaces.add(ifc);
				}
			}
			current = current.getSuperclass();
		}
		return interfaces;
	}

	/**
	 * 返回一个类型，该类型实现了所有给定的接口；这个方法可能乍一看很难理解，我们先来看看他的实现：
	 * Create a composite interface Class for the given interfaces,
	 * implementing the given interfaces in one single Class.
	 * This implementation builds a JDK proxy class for the given interfaces.
	 * @param interfaces the interfaces to merge
	 * @param classLoader the ClassLoader to create the composite Class in
	 * @return the merged interface as Class
	 * @throws IllegalArgumentException if the specified interfaces expose conflicting method signatures (or a similar constraint is violated)
	 * @see java.lang.reflect.Proxy#getProxyClass
	 */
	@SuppressWarnings("deprecation")  // on JDK 9
	public static Class<?> createCompositeInterface(Class<?>[] interfaces, @Nullable ClassLoader classLoader) {
		Assert.notEmpty(interfaces, "Interface array must not be empty");
		return Proxy.getProxyClass(classLoader, interfaces);
	}

	/**
	 * 取出两个指定类的共同父类。
	 * Determine the common ancestor of the given classes, if any.
	 * @param clazz1 the class to introspect
	 * @param clazz2 the other class to introspect
	 * @return the common ancestor (i.e. common superclass, one interface extending the other),
	 * or {@code null} if none found. If any of the given classes is {@code null}, the other class will be returned.
	 * @since 3.2.6
	 * @see org.springframework.util.ClassUtilsTests#testDetermineCommonAncestor() 【测试用例】
	 */
	@Nullable
	public static Class<?> determineCommonAncestor(@Nullable Class<?> clazz1, @Nullable Class<?> clazz2) {
		if (clazz1 == null) return clazz2;
		if (clazz2 == null) return clazz1;
		if (clazz1.isAssignableFrom(clazz2)) {
			return clazz1;
		}
		if (clazz2.isAssignableFrom(clazz1)) {
			return clazz2;
		}
		Class<?> ancestor = clazz1;
		do {
			ancestor = ancestor.getSuperclass();
			if (ancestor == null || Object.class == ancestor) return null;
		}
		while (!ancestor.isAssignableFrom(clazz2));
		return ancestor;
	}

	/**
	 * Determine whether the given interface is a common Java language interface:
	 * {@link Serializable}, {@link Externalizable}, {@link Closeable}, {@link AutoCloseable},
	 * {@link Cloneable}, {@link Comparable} - all of which can be ignored when looking
	 * for 'primary' user-level interfaces. Common characteristics: no service-level operations, no bean property methods, no default methods.
	 * @param ifc the interface to check
	 * @since 5.0.3
	 */
	public static boolean isJavaLanguageInterface(Class<?> ifc) {
		return javaLanguageInterfaces.contains(ifc);
	}

	/**
	 * Determine if the supplied class is an <em>inner class</em>,
	 * i.e. a non-static member of an enclosing class.
	 * @return {@code true} if the supplied class is an inner class
	 * @since 5.0.5
	 * @see Class#isMemberClass()
	 */
	public static boolean isInnerClass(Class<?> clazz) {
		return (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers()));
	}

	/**
	 * Check whether the given object is a CGLIB proxy.
	 * @param object the object to check
	 * @see #isCglibProxyClass(Class)
	 * @see org.springframework.aop.support.AopUtils#isCglibProxy(Object)
	 */
	public static boolean isCglibProxy(Object object) {
		return isCglibProxyClass(object.getClass());
	}

	/**
	 * Check whether the specified class is a CGLIB-generated class.
	 * @param clazz the class to check
	 * @see #isCglibProxyClassName(String)
	 */
	public static boolean isCglibProxyClass(@Nullable Class<?> clazz) {
		return (clazz != null && isCglibProxyClassName(clazz.getName()));
	}

	/**
	 * Check whether the specified class name is a CGLIB-generated class.
	 * @param className the class name to check
	 */
	public static boolean isCglibProxyClassName(@Nullable String className) {
		return (className != null && className.contains(CGLIB_CLASS_SEPARATOR));
	}

	/**
	 * 获取用户定义的本来的类型，大部分情况下就是类型本身，主要针对cglib做了额外的判断，获取cglib代理之后的父类；
	 * Return the user-defined class for the given instance: usually simply the class of the given instance,
	 * but the original class in case of a CGLIB-generated subclass.
	 * @param instance the instance to check
	 * @return the user-defined class
	 */
	public static Class<?> getUserClass(Object instance) {
		Assert.notNull(instance, "Instance must not be null");
		return getUserClass(instance.getClass());
	}

	/**
	 * Return the user-defined class for the given class: usually simply the given class, but the original class in case of a CGLIB-generated subclass.
	 * @param clazz the class to check
	 * @return the user-defined class
	 */
	public static Class<?> getUserClass(Class<?> clazz) {
		if (clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
			Class<?> superclass = clazz.getSuperclass();
			if (superclass != null && superclass != Object.class) {
				return superclass;
			}
		}
		return clazz;
	}

	/**
	 * 获取一个对象的描述类型；一般来说，就是类名，能够正确处理数组，如果是JDK代理对象，能够正确输出其接口类型：
	 * Return a descriptive name for the given object's type: usually simply the class name,
	 * but component type class name + "[]" for arrays,  and an appended list of implemented interfaces for JDK proxies.
	 * @param value the value to introspect
	 * @return the qualified name of the class
	 */
	@Nullable
	public static String getDescriptiveType(@Nullable Object value) {
		if (value == null) return null;
		Class<?> clazz = value.getClass();
		if (Proxy.isProxyClass(clazz)) {
			StringBuilder result = new StringBuilder(clazz.getName());
			result.append(" implementing ");
			Class<?>[] ifcs = clazz.getInterfaces();
			for (int i = 0; i < ifcs.length; i++) {
				result.append(ifcs[i].getName());
				if (i < ifcs.length - 1) result.append(',');
			}
			return result.toString();
		}else {
			return clazz.getTypeName();
		}
	}

	/**
	 * Check whether the given class matches the user-specified type name.
	 * @param clazz the class to check
	 * @param typeName the type name to match
	 */
	public static boolean matchesTypeName(Class<?> clazz, @Nullable String typeName) {
		return (typeName != null && (typeName.equals(clazz.getTypeName()) || typeName.equals(clazz.getSimpleName())));
	}

	/**
	 * 得到一个全限定类名的简写，可以处理简单类型和内部类的情况
	 * Get the class name without the qualified package name.
	 * @param className the className to get the short name for  	 输入："org.springframework.util.ClassUtilsTests"
	 * @return the class name of the class without the package name  结果： "ClassUtilsTests"
	 * @throws IllegalArgumentException if the className is empty
	 */
	public static String getShortName(String className) {
		Assert.hasLength(className, "Class name must not be empty");
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
		if (nameEndIndex == -1) nameEndIndex = className.length();
		String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
		shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
		return shortName;
	}

	/**
	 * 得到一个全限定类名的简写，可以处理简单类型和内部类的情况
	 * Get the class name without the qualified package name.
	 * @param clazz the class to get the short name for
	 * @return the class name of the class without the package name
	 */
	public static String getShortName(Class<?> clazz) {
		return getShortName(getQualifiedName(clazz));
	}

	/**
	 * 得到一个类的简写，并按照属性的方式来命名；
	 * Return the short string name of a Java class in uncapitalized JavaBeans property format.Strips the outer class name in case of an inner class.
	 * @param clazz the class  输入： " org.springframework.util.ClassUtilsTests"
	 * @return the short name rendered in a standard JavaBeans property format   结果："ClassUtilsTests"
	 * @see java.beans.Introspector#decapitalize(String)
	 */
	public static String getShortNameAsProperty(Class<?> clazz) {
		String shortName = getShortName(clazz);
		int dotIndex = shortName.lastIndexOf(PACKAGE_SEPARATOR);
		shortName = (dotIndex != -1 ? shortName.substring(dotIndex + 1) : shortName);
		return Introspector.decapitalize(shortName);
	}

	/**
	 * 获取类对应的类的字节码文件名称
	 * Determine the name of the class file, relative to the containing package: e.g. "String.class"
	 * @param clazz the class
	 * @return the file name of the ".class" file
	 */
	public static String getClassFileName(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return className.substring(lastDotIndex + 1) + CLASS_FILE_SUFFIX;
	}

	/**
	 * 根据类得到包名
	 * Determine the name of the package of the given class, e.g. "java.lang" for the {@code java.lang.String} class.
	 * @param clazz the class eg： String.class
	 * @return the package name, or the empty String if the class is defined in the default package eg： "java.lang"
	 */
	public static String getPackageName(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return getPackageName(clazz.getName());
	}

	/**
	 * 根据全限定类名得到包名
	 * Determine the name of the package of the given fully-qualified class name,
	 * e.g. "java.lang" for the {@code java.lang.String} class name.
	 * @param fqClassName the fully-qualified class name
	 * @return the package name, or the empty String if the class is defined in the default package
	 */
	public static String getPackageName(String fqClassName) {
		Assert.notNull(fqClassName, "Class name must not be null");
		int lastDotIndex = fqClassName.lastIndexOf(PACKAGE_SEPARATOR);
		return (lastDotIndex != -1 ? fqClassName.substring(0, lastDotIndex) : "");
	}

	/**
	 * 根据类得到类的权限定名；这个方法主要特点在于可以正确处理数组的类名
	 * Return the qualified name of the given class: usually simply the class name, but component type class name + "[]" for arrays.
	 * @param clazz the class
	 * @return the qualified name of the class
	 */
	public static String getQualifiedName(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		return clazz.getTypeName();
	}

	/**
	 * Return the qualified name of the given method, consisting of
	 * fully qualified interface/class name + "." + method name.
	 * @param method the method
	 * @return the qualified name of the method
	 */
	public static String getQualifiedMethodName(Method method) {
		return getQualifiedMethodName(method, null);
	}

	/**
	 * 获取方法的全名，包括类的权限定名.方法名；
	 * Return the qualified name of the given method, consisting of fully qualified interface/class name + "." + method name.
	 * @param method the method
	 * @param clazz the clazz that the method is being invoked on
	 * (may be {@code null} to indicate the method's declaring class)
	 * @return the qualified name of the method
	 * @since 4.3.4
	 */
	public static String getQualifiedMethodName(Method method, @Nullable Class<?> clazz) {
		Assert.notNull(method, "Method must not be null");
		return (clazz != null ? clazz : method.getDeclaringClass()).getName() + '.' + method.getName();
	}

	/**
	 * 判定是否存在指定构造器；
	 * Determine whether the given class has a public constructor with the given signature.
	 * Essentially translates {@code NoSuchMethodException} to "false".
	 * @param clazz the clazz to analyze
	 * @param paramTypes the parameter types of the method
	 * @return whether the class has a corresponding constructor
	 * @see Class#getMethod
	 */
	public static boolean hasConstructor(Class<?> clazz, Class<?>... paramTypes) {
		return (getConstructorIfAvailable(clazz, paramTypes) != null);
	}

	/**
	 * 得到一个类的构造器方法；对异常进行了拦截，在没有找到指定构造器的时候，返回null；
	 * Determine whether the given class has a public constructor with the given signature,and return it if available (else return {@code null}).
	 * Essentially translates {@code NoSuchMethodException} to {@code null}.
	 * @param clazz the clazz to analyze
	 * @param paramTypes the parameter types of the method
	 * @return the constructor, or {@code null} if not found
	 * @see Class#getConstructor
	 */
	@Nullable
	public static <T> Constructor<T> getConstructorIfAvailable(Class<T> clazz, Class<?>... paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		try {
			return clazz.getConstructor(paramTypes);
		}catch (NoSuchMethodException ex) {
			return null;
		}
	}

	/**
	 * 判断类是否有指定的public方法；
	 * Determine whether the given class has a public method with the given signature.
	 * Essentially translates {@code NoSuchMethodException} to "false".
	 * @param clazz the clazz to analyze
	 * @param methodName the name of the method
	 * @param paramTypes the parameter types of the method
	 * @return whether the class has a corresponding method
	 * @see Class#getMethod
	 */
	public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
		return (getMethodIfAvailable(clazz, methodName, paramTypes) != null);
	}

	/**
	 * 得到类上指定的public方法；其实现也是一个标准的反射使用：
	 * Determine whether the given class has a public method with the given signature, and return it if available (else throws an {@code IllegalStateException}).
	 * In case of any signature specified, only returns the method if there is a
	 * unique candidate, i.e. a single public method with the specified name.
	 * Essentially translates {@code NoSuchMethodException} to {@code IllegalStateException}.
	 * @param clazz the clazz to analyze
	 * @param methodName the name of the method
	 * @param paramTypes the parameter types of the method
	 * (may be {@code null} to indicate any signature)
	 * @return the method (never {@code null})
	 * @throws IllegalStateException if the method has not been found
	 * @see Class#getMethod
	 *
	 * 为什么不能使用clazz.getMethod(methodName)来获取没有参数列表的方法呢？
	 * 而要选择通过clazz.getMethods()先得到所有方法，然后再去对比方法名的方式来获取没有参数列表的方法呢？
	 * 其实就是为了处理重载的异常而已；
	 * 这些细节我觉得都展示出了在写一个框架或者只是一个工具类的时候的严谨思路；
	 */
	public static Method getMethod(Class<?> clazz, String methodName, @Nullable Class<?>... paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		if (paramTypes != null) {
			try {
				//如果指定了参数类型，直接使用getMethod方法；
				return clazz.getMethod(methodName, paramTypes);
			}catch (NoSuchMethodException ex) {
				throw new IllegalStateException("Expected method not found: " + ex);
			}
		}else {
			Set<Method> candidates = new HashSet<>(1);
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if (methodName.equals(method.getName())) {
					candidates.add(method);
				}
			}
			if (candidates.size() == 1) {
				return candidates.iterator().next();
			}else if (candidates.isEmpty()) {
				throw new IllegalStateException("Expected method not found: " + clazz.getName() + '.' + methodName);
			}else {
				throw new IllegalStateException("No unique method found: " + clazz.getName() + '.' + methodName);
			}
		}
	}

	/**
	 * 该方法类似Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) 方法，只是当出现重载的情况，不会抛出异常，返回找到的第一个方法返回；
	 * Determine whether the given class has a public method with the given signature,and return it if available (else return {@code null}).
	 * In case of any signature specified, only returns the method if there is a unique candidate, i.e. a single public method with the specified name.
	 * Essentially translates {@code NoSuchMethodException} to {@code null}.
	 * @param clazz the clazz to analyze
	 * @param methodName the name of the method
	 * @param paramTypes the parameter types of the method (may be {@code null} to indicate any signature)
	 * @return the method, or {@code null} if not found
	 * @see Class#getMethod
	 */
	@Nullable
	public static Method getMethodIfAvailable(Class<?> clazz, String methodName, @Nullable Class<?>... paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		if (paramTypes != null) {
			try {
				return clazz.getMethod(methodName, paramTypes);
			}catch (NoSuchMethodException ex) {
				return null;
			}
		}else {
			Set<Method> candidates = new HashSet<>(1);
			Method[] methods = clazz.getMethods();
			for (Method method : methods) {
				if (methodName.equals(method.getName())) {
					candidates.add(method);
				}
			}
			if (candidates.size() == 1) {
				return candidates.iterator().next();
			}
			return null;
		}
	}

	/**
	 * 获取指定类中匹配该方法名称的方法个数，包括非public方法；
	 * Return the number of methods with a given name (with any argument types),for the given class and/or its superclasses. Includes non-public methods.
	 * @param clazz	the clazz to check
	 * @param methodName the name of the method
	 * @return the number of methods with the given name
	 */
	public static int getMethodCountForName(Class<?> clazz, String methodName) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		int count = 0;
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (methodName.equals(method.getName())) {
				count++;
			}
		}
		Class<?>[] ifcs = clazz.getInterfaces();
		for (Class<?> ifc : ifcs) {
			count += getMethodCountForName(ifc, methodName);
		}
		if (clazz.getSuperclass() != null) {
			count += getMethodCountForName(clazz.getSuperclass(), methodName);
		}
		return count;
	}

	/**
	 * 判定指定的类及其父类中是否包含指定方法名称的方法，包括非public方法；我们又来看看这个方法的实现：
	 * Does the given class or one of its superclasses at least have one or more methods with the supplied name (with any argument types)?
	 * Includes non-public methods.
	 * @param clazz	the clazz to check
	 * @param methodName the name of the method
	 * @return whether there is at least one method with the given name
	 */
	public static boolean hasAtLeastOneMethodWithName(Class<?> clazz, String methodName) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		Method[] declaredMethods = clazz.getDeclaredMethods();
		//判断当前类是否包含指定方法；
		for (Method method : declaredMethods) {
			if (method.getName().equals(methodName)) {
				return true;
			}
		}
		Class<?>[] ifcs = clazz.getInterfaces();
		//递归判断当前类实现的接口上是否有该方法；
		for (Class<?> ifc : ifcs) {
			if (hasAtLeastOneMethodWithName(ifc, methodName)) {
				return true;
			}
		}
		//递归判定当前类的父类上是否有该方法；
		return (clazz.getSuperclass() != null && hasAtLeastOneMethodWithName(clazz.getSuperclass(), methodName));
	}

	/**
	 * 获得最匹配的一个可以执行的方法；比如传入IEmployeeService.someLogic方法，在EmployeeServiceImpl类上找到匹配的EmployeeServiceImpl.someLogic方法，这个方法是一个可以执行的方法；
	 * Given a method, which may come from an interface, and a target class used in the current reflective invocation,
	 * find the corresponding target method  if there is one.
	 * E.g. the method may be {@code IFoo.bar()} and the target class may be {@code DefaultFoo}. In this case, the method may be {@code DefaultFoo.bar()}.
	 * This enables attributes on that method to be found.
	 * <b>NOTE:</b> In contrast to {@link org.springframework.aop.support.AopUtils#getMostSpecificMethod},this method does <i>not</i> resolve Java 5 bridge methods automatically.
	 * Call {@link org.springframework.core.BridgeMethodResolver#findBridgedMethod}
	 * if bridge method resolution is desirable (e.g. for obtaining metadata from the original method definition).
	 * <b>NOTE:</b> Since Spring 3.1.1, if Java security settings disallow reflective access (e.g. calls to {@code Class#getDeclaredMethods} etc,
	 * this implementation will fall back to returning the originally provided method.
	 * @param method the method to be invoked, which may come from an interface
	 * @param targetClass the target class for the current invocation (may be {@code null} or may not even implement the method)
	 * @return the specific target method, or the original method if the {@code targetClass} does not implement it
	 * @see #getInterfaceMethodIfPossible
	 */
	public static Method getMostSpecificMethod(Method method, @Nullable Class<?> targetClass) {
		if (targetClass != null && targetClass != method.getDeclaringClass() && isOverridable(method, targetClass)) {
			try {
				if (Modifier.isPublic(method.getModifiers())) {
					try {
						return targetClass.getMethod(method.getName(), method.getParameterTypes());
					}catch (NoSuchMethodException ex) {
						return method;
					}
				}else {
					Method specificMethod = ReflectionUtils.findMethod(targetClass, method.getName(), method.getParameterTypes());
					return (specificMethod != null ? specificMethod : method);
				}
			}catch (SecurityException ex) {
				// Security settings are disallowing reflective access; fall back to 'method' below.
			}
		}
		return method;
	}

	/**
	 * Determine a corresponding interface method for the given method handle, if possible.
	 * This is particularly useful for arriving at a public exported type on Jigsaw
	 * which can be reflectively invoked without an illegal access warning.
	 * @param method the method to be invoked, potentially from an implementation class
	 * @return the corresponding interface method, or the original method if none found
	 * @since 5.1
	 * @see #getMostSpecificMethod
	 */
	public static Method getInterfaceMethodIfPossible(Method method) {
		if (Modifier.isPublic(method.getModifiers()) && !method.getDeclaringClass().isInterface()) {
			Class<?> current = method.getDeclaringClass();
			while (current != null && current != Object.class) {
				Class<?>[] ifcs = current.getInterfaces();
				for (Class<?> ifc : ifcs) {
					try {
						return ifc.getMethod(method.getName(), method.getParameterTypes());
					}catch (NoSuchMethodException ex) {
						// ignore
					}
				}
				current = current.getSuperclass();
			}
		}
		return method;
	}

	/**
	 * 该方法用于判定一个方法是否是用户可用的方法
	 * Determine whether the given method is declared by the user or at least pointing to a user-declared method.
	 * Checks {@link Method#isSynthetic()} (for implementation methods) as well as the
	 * {@code GroovyObject} interface (for interface methods; on an implementation class,
	 * implementations of the {@code GroovyObject} methods will be marked as synthetic anyway).
	 * Note that, despite being synthetic, bridge methods ({@link Method#isBridge()}) are considered
	 * as user-level methods since they are eventually pointing to a user-declared generic method.
	 * @param method the method to check
	 * @return {@code true} if the method can be considered as user-declared; [@code false} otherwise
	 * 1.method.isBridge：判定一个方法是否是桥接方法。什么是bridge方法？这个是JDK1.5引入了泛型之后的概念。为了让1.5泛型方法和1.5之前的字节码保持兼容，编译器会自动的生成桥接方法；
	 * 2.method. isSynthetic方法：判定一个方法是否是虚构方法（synthetic method）；什么是synthetic方法？
	 * 由编译器创建的，非默认构造方法（我们知道，类都有默认构造方法，当然重载了默认构造方法的除外，编译器都会生成一个默认构造方法的实现）
	 * 在源码中没有对应的方法实现的方法都是虚构方法。比如上面介绍的bridge方法就是一个典型的synthetic方法；
	 * 3.isGroovyObjectMethod：判定一个方法是否是Groovy的方法，因为Spring支持Groovy，而Groovy的类都实现了groovy.lang.GroovyObject类；关于Groovy可以自己去了解一下；
	 */
	public static boolean isUserLevelMethod(Method method) {
		Assert.notNull(method, "Method must not be null");
		return (method.isBridge() || (!method.isSynthetic() && !isGroovyObjectMethod(method)));
	}

	private static boolean isGroovyObjectMethod(Method method) {
		return method.getDeclaringClass().getName().equals("groovy.lang.GroovyObject");
	}

	/**
	 * Determine whether the given method is overridable in the given target class.
	 * @param method the method to check
	 * @param targetClass the target class to check against
	 */
	private static boolean isOverridable(Method method, @Nullable Class<?> targetClass) {
		if (Modifier.isPrivate(method.getModifiers())) {
			return false;
		}
		if (Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers())) {
			return true;
		}
		return (targetClass == null || getPackageName(method.getDeclaringClass()).equals(getPackageName(targetClass)));
	}

	/**
	 * 针对给定的类和方法名字，参数类型列表，得到一个对应的static方法
	 * Return a public static method of a class.
	 * @param clazz the class which defines the method
	 * @param methodName the static method name
	 * @param args the parameter types to the method
	 * @return the static method, or {@code null} if no static method was found
	 * @throws IllegalArgumentException if the method name is blank or the clazz is null
	 * @see org.springframework.util.ClassUtilsTests#testNoArgsStaticMethod()  【测试用例】
	 */
	@Nullable
	public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>... args) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(methodName, "Method name must not be null");
		try {
			Method method = clazz.getMethod(methodName, args);
			// Modifier是一个我们很少接触的类；这个类可以用来判定修饰符
			return Modifier.isStatic(method.getModifiers()) ? method : null;
		}catch (NoSuchMethodException ex) {
			return null;
		}
	}
}
