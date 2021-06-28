

package org.springframework.util;

import org.springframework.lang.Nullable;

import java.lang.reflect.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Simple utility class for working with the reflection API and handling reflection exceptions.Only intended for internal use.
 * @since 1.2.2
 */
public abstract class ReflectionUtils {

	/**
	 * Pre-built MethodFilter that matches all non-bridge methods.
	 * @since 3.0
	 * @deprecated as of 5.0.11, in favor of a custom {@link MethodFilter}
	 */
	@Deprecated
	public static final MethodFilter NON_BRIDGED_METHODS = (method -> !method.isBridge());

	/**
	 * Pre-built MethodFilter that matches all non-bridge non-synthetic methods
	 * which are not declared on {@code java.lang.Object}.
	 * @since 3.0.5
	 */
	public static final MethodFilter USER_DECLARED_METHODS = (method -> !method.isBridge() && !method.isSynthetic() && method.getDeclaringClass() != Object.class);

	/**
	 * Pre-built FieldFilter that matches all non-static, non-final fields.
	 */
	public static final FieldFilter COPYABLE_FIELDS = (field -> !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())));

	/**
	 * Naming prefix for CGLIB-renamed methods.
	 * @see #isCglibRenamedMethod
	 */
	private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";

	private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];

	private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];

	/**
	 * Cache for {@link Class#getDeclaredMethods()} plus equivalent default methods from Java 8 based interfaces, allowing for fast iteration.
	 */
	private static final Map<Class<?>, Method[]> declaredMethodsCache = new ConcurrentReferenceHashMap<>(256);

	/**
	 * Cache for {@link Class#getDeclaredFields()}, allowing for fast iteration.
	 */
	private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentReferenceHashMap<>(256);

	// Exception handling

	/**
	 * 处理反射中的异常
	 * 统一下来，可以这样理解，除了在反射执行过程中遇到的Error，其余所有的Exception，都被统一转成了RuntimeException；
	 * Handle the given reflection exception. Should only be called if no checked exception is expected to be thrown by the target method.
	 * Throws the underlying RuntimeException or Error in case of an InvocationTargetException with such a root cause.
	 * Throws an IllegalStateException with an appropriate message or UndeclaredThrowableException otherwise.
	 * @param ex the reflection exception to handle
	 */
	public static void handleReflectionException(Exception ex) {
		if (ex instanceof NoSuchMethodException) {
			throw new IllegalStateException("Method not found: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access method: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * Handle the given invocation target exception. Should only be called if no
	 * checked exception is expected to be thrown by the target method.
	 * Throws the underlying RuntimeException or Error in case of such a root
	 * cause. Throws an UndeclaredThrowableException otherwise.
	 * @param ex the invocation target exception to handle
	 */
	public static void handleInvocationTargetException(InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the <em>target exception</em> of an {@link InvocationTargetException}.
	 * Should only be called if no checked exception is expected to be thrown  by the target method.
	 * Rethrows the underlying exception cast to a {@link RuntimeException} or {@link Error} if appropriate;
	 * otherwise, throws an {@link UndeclaredThrowableException}.
	 * @param ex the exception to rethrow
	 * @throws RuntimeException the rethrown exception
	 * 该方法只是判断了运行时异常和Error；并原样抛出；怎么理解这个方法的调用？
	 * 原因很简单，InvocationTargetException是在method.invoke的时候抛出的，
	 * 方法在执行的过程中，方法本身的执行可能抛出RuntimeException或者Error，
	 * 其余方法本身抛出的Exception异常直接包装为UndeclaredThrowableException（RuntimeException）处理；
	 */
	public static void rethrowRuntimeException(Throwable ex) {
		if (ex instanceof RuntimeException) throw (RuntimeException) ex;
		if (ex instanceof Error) 			throw (Error) ex;
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * Rethrow the given {@link Throwable exception}, which is presumably the
	 * <em>target exception</em> of an {@link InvocationTargetException}.
	 * Should only be called if no checked exception is expected to be thrown
	 * by the target method.
	 * Rethrows the underlying exception cast to an {@link Exception} or
	 * {@link Error} if appropriate; otherwise, throws an
	 * {@link UndeclaredThrowableException}.
	 * @param ex the exception to rethrow
	 * @throws Exception the rethrown exception (in case of a checked exception)
	 */
	public static void rethrowException(Throwable ex) throws Exception {
		if (ex instanceof Exception) throw (Exception) ex;
		if (ex instanceof Error) throw (Error) ex;
		throw new UndeclaredThrowableException(ex);
	}

	// Constructor handling
	/**
	 * Obtain an accessible constructor for the given class and parameters.
	 * @param clazz the clazz to check
	 * @param parameterTypes the parameter types of the desired constructor
	 * @return the constructor reference
	 * @throws NoSuchMethodException if no such constructor exists
	 * @since 5.0
	 */
	public static <T> Constructor<T> accessibleConstructor(Class<T> clazz, Class<?>... parameterTypes)	throws NoSuchMethodException {
		Constructor<T> ctor = clazz.getDeclaredConstructor(parameterTypes);
		makeAccessible(ctor);
		return ctor;
	}

	/**
	 * 将一个构造器设置为可调用，主要针对private构造器；
	 * Make the given constructor accessible, explicitly setting it accessible if necessary.
	 * The {@code setAccessible(true)} method is only called when actually necessary,
	 * to avoid unnecessary conflicts with a JVM SecurityManager (if active).
	 * @param ctor the constructor to make accessible
	 * @see java.lang.reflect.Constructor#setAccessible
	 */
	@SuppressWarnings("deprecation")  // on JDK 9
	public static void makeAccessible(Constructor<?> ctor) {
		if ((!Modifier.isPublic(ctor.getModifiers()) ||	!Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
			ctor.setAccessible(true);
		}
	}

	// Method handling
	/**
	 * Attempt to find a {@link Method} on the supplied class with the supplied name and no parameters. Searches all superclasses up to {@code Object}.
	 * Returns {@code null} if no {@link Method} can be found.
	 * @param clazz the class to introspect
	 * @param name the name of the method
	 * @return the Method object, or {@code null} if none found
	 */
	@Nullable
	public static Method findMethod(Class<?> clazz, String name) {
		return findMethod(clazz, name, new Class<?>[0]);
	}

	/**
	 *  在类型clazz上，查询name方法，参数类型列表为paramTypes；
	 * Attempt to find a {@link Method} on the supplied class with the supplied name  and parameter types. Searches all superclasses up to {@code Object}.
	 * Returns {@code null} if no {@link Method} can be found.
	 * @param clazz the class to introspect
	 * @param name the name of the method
	 * @param paramTypes the parameter types of the method
	 * (may be {@code null} to indicate any signature)
	 * @return the Method object, or {@code null} if none found
	 */
	@Nullable
	public static Method findMethod(Class<?> clazz, String name, @Nullable Class<?>... paramTypes) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.notNull(name, "Method name must not be null");
		Class<?> searchType = clazz;
		while (searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
			for (Method method : methods) {
				if (name.equals(method.getName()) && (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
					return method;
				}
			}
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * Invoke the specified {@link Method} against the supplied target object with no arguments.
	 * The target object can be {@code null} when invoking a static {@link Method}.
	 * Thrown exceptions are handled via a call to {@link #handleReflectionException}.
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @return the invocation result, if any
	 * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
	 */
	@Nullable
	public static Object invokeMethod(Method method, @Nullable Object target) {
		return invokeMethod(method, target, new Object[0]);
	}

	/**
	 * 在指定对象(target)上，使用指定参数(args)，执行方法（method)；
	 * Invoke the specified {@link Method} against the supplied target object with the supplied arguments.
	 * The target object can be {@code null} when invoking a static {@link Method}.Thrown exceptions are handled via a call to {@link #handleReflectionException}.
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @param args the invocation arguments (may be {@code null})
	 * @return the invocation result, if any
	 */
	@Nullable
	public static Object invokeMethod(Method method, @Nullable Object target, @Nullable Object... args) {
		try {
			return method.invoke(target, args);
		}catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * Invoke the specified JDBC API {@link Method} against the supplied target object with no arguments.
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @return the invocation result, if any
	 * @throws SQLException the JDBC API SQLException to rethrow (if any)
	 * @see #invokeJdbcMethod(java.lang.reflect.Method, Object, Object[])
	 * @deprecated as of 5.0.11, in favor of custom SQLException handling
	 */
	@Deprecated
	@Nullable
	public static Object invokeJdbcMethod(Method method, @Nullable Object target) throws SQLException {
		return invokeJdbcMethod(method, target, new Object[0]);
	}

	/**
	 * Invoke the specified JDBC API {@link Method} against the supplied target
	 * object with the supplied arguments.
	 * @param method the method to invoke
	 * @param target the target object to invoke the method on
	 * @param args the invocation arguments (may be {@code null})
	 * @return the invocation result, if any
	 * @throws SQLException the JDBC API SQLException to rethrow (if any)
	 * @see #invokeMethod(java.lang.reflect.Method, Object, Object[])
	 * @deprecated as of 5.0.11, in favor of custom SQLException handling
	 */
	@Deprecated
	@Nullable
	public static Object invokeJdbcMethod(Method method, @Nullable Object target, @Nullable Object... args) throws SQLException {
		try {
			return method.invoke(target, args);
		}catch (IllegalAccessException ex) {
			handleReflectionException(ex);
		}catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof SQLException) {
				throw (SQLException) ex.getTargetException();
			}
			handleInvocationTargetException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * 判断一个方法上是否声明了指定类型的异常；
	 * Determine whether the given method explicitly declares the given  exception or one of its superclasses,
	 * which means that an exception of that type can be propagated as-is within a reflective invocation.
	 * @param method the declaring method
	 * @param exceptionType the exception to throw
	 * @return {@code true} if the exception can be thrown as-is;{@code false} if it needs to be wrapped
	 */
	public static boolean declaresException(Method method, Class<?> exceptionType) {
		Assert.notNull(method, "Method must not be null");
		Class<?>[] declaredExceptions = method.getExceptionTypes();
		for (Class<?> declaredException : declaredExceptions) {
			if (declaredException.isAssignableFrom(exceptionType)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 针对指定类型上的所有方法，依次调用MethodCallback回调
	 * 就是得到类上的所有方法，然后执行回调接口；这个方法在Spring针对bean的方法上的标签处理时大量使用，比如@Init，@Resource，@Autowire等标签的预处理；
	 * Perform the given callback operation on all matching methods of the given class,
	 * as locally declared or equivalent thereof (such as default methods on Java 8 based interfaces that the given class implements).
	 * @param clazz the class to introspect
	 * @param mc the callback to invoke for each method
	 * @throws IllegalStateException if introspection fails
	 * @since 4.2
	 * @see #doWithMethods
	 */
	public static void doWithLocalMethods(Class<?> clazz, MethodCallback mc) {
		Method[] methods = getDeclaredMethods(clazz);
		for (Method method : methods) {
			try {
				mc.doWith(method);
			}catch (IllegalAccessException ex) {
				throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
			}
		}
	}

	/**
	 * Perform the given callback operation on all matching methods of the given
	 * class and superclasses.
	 * The same named method occurring on subclass and superclass will appear
	 * twice, unless excluded by a {@link MethodFilter}.
	 * @param clazz the class to introspect
	 * @param mc the callback to invoke for each method
	 * @throws IllegalStateException if introspection fails
	 * @see #doWithMethods(Class, MethodCallback, MethodFilter)
	 */
	public static void doWithMethods(Class<?> clazz, MethodCallback mc) {
		doWithMethods(clazz, mc, null);
	}

	/**
	 * 该版本提供了一个方法匹配（过滤器）MethodFilter； 该方法会递归向上查询所有父类和实现的接口上的所有方法并处理；
	 * Perform the given callback operation on all matching methods of the given
	 * class and superclasses (or given interface and super-interfaces).
	 * The same named method occurring on subclass and superclass will appear
	 * twice, unless excluded by the specified {@link MethodFilter}.
	 * @param clazz the class to introspect
	 * @param mc the callback to invoke for each method
	 * @param mf the filter that determines the methods to apply the callback to
	 * @throws IllegalStateException if introspection fails
	 */
	public static void doWithMethods(Class<?> clazz, MethodCallback mc, @Nullable MethodFilter mf) {
		// Keep backing up the inheritance hierarchy. 首先得到类上所有方法
		Method[] methods = getDeclaredMethods(clazz);
		for (Method method : methods) {
			// 针对每一个方法，调用MethodFilter实现匹配检查,
			if (mf != null && !mf.matches(method)) {
				continue;
			}
			try {
				// 如果匹配上，调用MethodCallback回调方法
				mc.doWith(method);
			}catch (IllegalAccessException ex) {
				throw new IllegalStateException("Not allowed to access method '" + method.getName() + "': " + ex);
			}
		}
		if (clazz.getSuperclass() != null) {
			doWithMethods(clazz.getSuperclass(), mc, mf);
		}else if (clazz.isInterface()) {
			for (Class<?> superIfc : clazz.getInterfaces()) {
				doWithMethods(superIfc, mc, mf);
			}
		}
	}

	/**
	 * Get all declared methods on the leaf class and all superclasses.
	 * Leaf class methods are included first.
	 * @param leafClass the class to introspect
	 * @throws IllegalStateException if introspection fails
	 */
	public static Method[] getAllDeclaredMethods(Class<?> leafClass) {
		final List<Method> methods = new ArrayList<>(32);
		doWithMethods(leafClass, methods::add);
		return methods.toArray(EMPTY_METHOD_ARRAY);
	}

	/**
	 * Get the unique set of declared methods on the leaf class and all superclasses.
	 * Leaf class methods are included first and while traversing the superclass hierarchy
	 * any methods found with signatures matching a method already included are filtered out.
	 * @param leafClass the class to introspect
	 * @throws IllegalStateException if introspection fails
	 */
	public static Method[] getUniqueDeclaredMethods(Class<?> leafClass) {
		final List<Method> methods = new ArrayList<>(32);
		doWithMethods(leafClass, method -> {
			boolean knownSignature = false;
			Method methodBeingOverriddenWithCovariantReturnType = null;
			for (Method existingMethod : methods) {
				if (method.getName().equals(existingMethod.getName()) && Arrays.equals(method.getParameterTypes(), existingMethod.getParameterTypes())) {
					// Is this a covariant return type situation?
					if (existingMethod.getReturnType() != method.getReturnType() &&	existingMethod.getReturnType().isAssignableFrom(method.getReturnType())) {
						methodBeingOverriddenWithCovariantReturnType = existingMethod;
					}else {
						knownSignature = true;
					}
					break;
				}
			}
			if (methodBeingOverriddenWithCovariantReturnType != null) {
				methods.remove(methodBeingOverriddenWithCovariantReturnType);
			}
			if (!knownSignature && !isCglibRenamedMethod(method)) {
				methods.add(method);
			}
		});
		return methods.toArray(EMPTY_METHOD_ARRAY);
	}

	/**
	 * 获取jdk原生获取的方法集合，再加上当前类的父级中的default和static方法。 （缓存功能）
	 * This variant retrieves {@link Class#getDeclaredMethods()} from a local cache in order to avoid the JVM's SecurityManager check and defensive array copying.
	 * In addition, it also includes Java 8 default methods from locally implemented interfaces,since those are effectively to be treated just like declared methods.
	 * @param clazz the class to introspect
	 * @return the cached array of methods
	 * @throws IllegalStateException if introspection fails
	 * @see Class#getDeclaredMethods()
	 * 此变体从本地缓存检索{@link Class#getDeclaredMethods（）}，以避免JVM的SecurityManager检查和防御性数组复制。
	 * 此外，它还包括来自本地实现接口的Java8默认方法，因为这些方法可以像声明的方法一样有效地处理。
	 * @see org.springframework.util.ReflectionUtilsTests#testGetDeclaredMethods() 【测试用例】
	 */
	// -modify  private -> public
	public static Method[] getDeclaredMethods(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		Method[] result = declaredMethodsCache.get(clazz);
		if (result == null) {
			try {
				// 1.jdk原生方法获取的方法集合
				Method[] declaredMethods = clazz.getDeclaredMethods();
				List<Method> defaultMethods = findConcreteMethodsOnInterfaces(clazz);
				// 2.如果获取到 static/default方法，则将其添加到1的集合中
				if (defaultMethods != null) {
					result = new Method[declaredMethods.length + defaultMethods.size()];
					System.arraycopy(declaredMethods, 0, result, 0, declaredMethods.length);
					int index = declaredMethods.length;
					for (Method defaultMethod : defaultMethods) {
						result[index] = defaultMethod;
						index++;
					}
				}else {
					// 如果没有获取到，则直接返回1的集合。
					result = declaredMethods;
				}
				declaredMethodsCache.put(clazz, (result.length == 0 ? EMPTY_METHOD_ARRAY : result));
			}catch (Throwable ex) {
				throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
			}
		}
		return result;
	}

	/**
	 * 获取当前类/接口的父级类/接口中的static和default方法，因为接口中只有3中方法：abstract、static、default。
	 * 只是上一级（父级）查找，不会无限向上递归。
	 * @param clazz 待查找的类或接口
	 * @see org.springframework.util.ReflectionUtilsTests#testFindConcreteMethodsOnInterfaces() 【测试用例】
	*/
	@Nullable // - modify  private - > public
	public static List<Method> findConcreteMethodsOnInterfaces(Class<?> clazz) {
		List<Method> result = null;
		for (Class<?> ifc : clazz.getInterfaces()) {
			for (Method ifcMethod : ifc.getMethods()) {
				// 当前的方法不是抽象的方法就有默认的实现
				if (!Modifier.isAbstract(ifcMethod.getModifiers())) {
					if (result == null) {
						result = new ArrayList<>();
					}
					result.add(ifcMethod);
				}
			}
		}
		return result;
	}

	// 在AopUtils中也有这几个isXXX方法，是的，其实AopUtils中的isXXX方法就是调用的ReflectionUtils的这几个方法的；
	/**
	 * 判断方法是否是equals方法；
	 * Determine whether the given method is an "equals" method.
	 * @see java.lang.Object#equals(Object)
	 */
	public static boolean isEqualsMethod(@Nullable Method method) {
		if (method == null || !method.getName().equals("equals")) {
			return false;
		}
		Class<?>[] paramTypes = method.getParameterTypes();
		return (paramTypes.length == 1 && paramTypes[0] == Object.class);
	}

	/**
	 * 判断方法是否是hashcode方法；
	 * Determine whether the given method is a "hashCode" method.
	 * @see java.lang.Object#hashCode()
	 */
	public static boolean isHashCodeMethod(@Nullable Method method) {
		return (method != null && method.getName().equals("hashCode") && method.getParameterCount() == 0);
	}

	/**
	 * 判断方法是否是toString方法；
	 * Determine whether the given method is a "toString" method.
	 * @see java.lang.Object#toString()
	 */
	public static boolean isToStringMethod(@Nullable Method method) {
		return (method != null && method.getName().equals("toString") && method.getParameterCount() == 0);
	}

	/**
	 * 判断方法是否是Object类上的方法；
	 * Determine whether the given method is originally declared by {@link java.lang.Object}.
	 */
	public static boolean isObjectMethod(@Nullable Method method) {
		return (method != null && (method.getDeclaringClass() == Object.class || isEqualsMethod(method) || isHashCodeMethod(method) || isToStringMethod(method)));
	}

	/**
	 * Determine whether the given method is a CGLIB 'renamed' method, following the pattern "CGLIB$methodName$0".
	 * @param renamedMethod the method to check
	 */
	public static boolean isCglibRenamedMethod(Method renamedMethod) {
		String name = renamedMethod.getName();
		if (name.startsWith(CGLIB_RENAMED_METHOD_PREFIX)) {
			int i = name.length() - 1;
			while (i >= 0 && Character.isDigit(name.charAt(i))) {
				i--;
			}
			return (i > CGLIB_RENAMED_METHOD_PREFIX.length() && (i < name.length() - 1) && name.charAt(i) == '$');
		}
		return false;
	}

	/**
	 * 将一个方法设置为可调用，主要针对private方法；
	 * Make the given method accessible, explicitly setting it accessible if necessary.
	 * The {@code setAccessible(true)} method is only called when actually necessary, to avoid unnecessary conflicts with a JVM  SecurityManager (if active).
	 * @param method the method to make accessible
	 * @see java.lang.reflect.Method#setAccessible
	 */
	@SuppressWarnings("deprecation")  // on JDK 9
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * 根据字段名称，查询当前类及递归父类中的字段。
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with the supplied {@code name}.
	 * Searches all superclasses up to {@link Object}.
	 * @param clazz the class to introspect
	 * @param name the name of the field
	 * @return the corresponding Field object, or {@code null} if not found
	 * @see org.springframework.util.ReflectionUtilsTests#findField() 【测试用例】
	 */
	@Nullable
	public static Field findField(Class<?> clazz, String name) {
		return findField(clazz, name, null);
	}

	/**
	 * 根据字段名称或类型，查询当前类及递归父类中的字段。
	 * Attempt to find a {@link Field field} on the supplied {@link Class} with the supplied {@code name} and/or {@link Class type}.
	 * Searches all superclasses up to {@link Object}.
	 * @param clazz the class to introspect   待暴露其内部属性的类
	 * @param name the name of the field (may be {@code null} if type is specified)  字段名称
	 * @param type the type of the field (may be {@code null} if name is specified)  字段类型
	 * @return the corresponding Field object, or {@code null} if not found
	 * @see org.springframework.util.ReflectionUtilsTests#findField() 【测试用例】
	 */
	@Nullable
	public static Field findField(Class<?> clazz, @Nullable String name, @Nullable Class<?> type) {
		Assert.notNull(clazz, "Class must not be null");
		Assert.isTrue(name != null || type != null, "Either name or type of the field must be specified");
		Class<?> searchType = clazz;
		while (Object.class != searchType && searchType != null) {
			// 只获取当前类的所有字段
			Field[] fields = getDeclaredFields(searchType);
			for (Field field : fields) {
				// 这行代码还是很有讲究的，如果name传入null，那么只判断到name == null后面的name.equals不会报空指针异常。
				if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
					return field;
				}
			}
			// 递归向上查询当前类的所有字段，直到Object类型
			searchType = searchType.getSuperclass();
		}
		return null;
	}

	/**
	 * 在指定对象（target）中给指定字段（field）设置指定值（value）；
	 * Set the field represented by the supplied {@link Field field object} on the specified {@link Object target object} to the specified {@code value}.
	 * In accordance with {@link Field#set(Object, Object)} semantics, the new value is automatically unwrapped if the underlying field has a primitive type.
	 * Thrown exceptions are handled via a call to {@link #handleReflectionException(Exception)}.
	 * @param field the field to set
	 * @param target the target object on which to set the field
	 * @param value the value to set (may be {@code null})
	 */
	public static void setField(Field field, @Nullable Object target, @Nullable Object value) {
		try {
			field.set(target, value);
		}catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * 在指定对象（target)上得到指定字段(field)的值；
	 * Get the field represented by the supplied {@link Field field object} on the specified {@link Object target object}.
	 * In accordance with {@link Field#get(Object)} semantics,
	 * the returned value is automatically wrapped if the underlying field has a primitive type.
	 * Thrown exceptions are handled via a call to {@link #handleReflectionException(Exception)}.
	 * @param field the field to get
	 * @param target the target object from which to get the field
	 * @return the field's current value
	 */
	@Nullable
	public static Object getField(Field field, @Nullable Object target) {
		try {
			return field.get(target);
		}catch (IllegalAccessException ex) {
			// 使用了handleReflectionException方法来统一处理异常；
			handleReflectionException(ex);
			throw new IllegalStateException("Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * 那很明显，该方法就是针对所有的字段，执行的对应的回调了，这里的FieldCallback就类似于前面的MethodCallback：
	 * Invoke the given callback on all locally declared fields in the given class.
	 * @param clazz the target class to analyze
	 * @param fc the callback to invoke for each field
	 * @throws IllegalStateException if introspection fails
	 * @since 4.2
	 * @see #doWithFields
	 */
	public static void doWithLocalFields(Class<?> clazz, FieldCallback fc) {
		for (Field field : getDeclaredFields(clazz)) {
			try {
				fc.doWith(field);
			}catch (IllegalAccessException ex) {
				throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
			}
		}
	}

	/**
	 * Invoke the given callback on all fields in the target class, going up the
	 * class hierarchy to get all declared fields.
	 * @param clazz the target class to analyze
	 * @param fc the callback to invoke for each field
	 * @throws IllegalStateException if introspection fails
	 */
	public static void doWithFields(Class<?> clazz, FieldCallback fc) {
		doWithFields(clazz, fc, null);
	}

	/**
	 * Invoke the given callback on all fields in the target class, going up the
	 * class hierarchy to get all declared fields.
	 * @param clazz the target class to analyze
	 * @param fc the callback to invoke for each field
	 * @param ff the filter that determines the fields to apply the callback to
	 * @throws IllegalStateException if introspection fails
	 */
	public static void doWithFields(Class<?> clazz, FieldCallback fc, @Nullable FieldFilter ff) {
		// Keep backing up the inheritance hierarchy.
		Class<?> targetClass = clazz;
		do {
			Field[] fields = getDeclaredFields(targetClass);
			for (Field field : fields) {
				if (ff != null && !ff.matches(field)) {
					continue;
				}
				try {
					fc.doWith(field);
				}catch (IllegalAccessException ex) {
					throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
				}
			}
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);
	}

	/**
	 * 获取指定类中的所有字段，只获取当前类！（缓存功能）
	 * This variant retrieves {@link Class#getDeclaredFields()} from a local cache in order to avoid the JVM's SecurityManager check and defensive array copying.
	 * @param clazz the class to introspect
	 * @return the cached array of fields
	 * @throws IllegalStateException if introspection fails
	 * @see Class#getDeclaredFields()
	 * @see org.springframework.util.ReflectionUtilsTests#testGetDeclaredFields() 【测试用例】
	 */
	// modify private -> public
	public static Field[] getDeclaredFields(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		// 对类型和字段做了缓存，保存到了declaredFieldsCache中
		Field[] result = declaredFieldsCache.get(clazz);
		if (result == null) {
			try {
				result = clazz.getDeclaredFields();
				declaredFieldsCache.put(clazz, (result.length == 0 ? EMPTY_FIELD_ARRAY : result));
			}catch (Throwable ex) {
				throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +"] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
			}
		}
		return result;
	}

	/**
	 * Given the source object and the destination, which must be the same class
	 * or a subclass, copy all fields, including inherited fields. Designed to
	 * work on objects with public no-arg constructors.
	 * @throws IllegalStateException if introspection fails
	 */
	public static void shallowCopyFieldState(final Object src, final Object dest) {
		Assert.notNull(src, "Source for field copy cannot be null");
		Assert.notNull(dest, "Destination for field copy cannot be null");
		if (!src.getClass().isAssignableFrom(dest.getClass())) {
			throw new IllegalArgumentException("Destination class [" + dest.getClass().getName() + "] must be same or subclass as source class [" + src.getClass().getName() + "]");
		}
		doWithFields(src.getClass(), field -> {
			makeAccessible(field);
			Object srcValue = field.get(src);
			field.set(dest, srcValue);
		}, COPYABLE_FIELDS);
	}

	/**
	 * 判断字段是否是public static final的；
	 * Determine whether the given field is a "public static final" constant.
	 * @param field the field to check
	 */
	public static boolean isPublicStaticFinal(Field field) {
		int modifiers = field.getModifiers();
		return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers));
	}

	/**
	 * 将一个字段设置为可读写，主要针对private字段；
	 * Make the given field accessible, explicitly setting it accessible if necessary.
	 * The {@code setAccessible(true)} method is only called when actually necessary,
	 * to avoid unnecessary conflicts with a JVM SecurityManager (if active).
	 * @param field the field to make accessible
	 * @see java.lang.reflect.Field#setAccessible
	 */
	@SuppressWarnings("deprecation")  // on JDK 9
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	// Cache handling
	/**
	 * Clear the internal method/field cache.
	 * @since 4.2.4
	 */
	public static void clearCache() {
		declaredMethodsCache.clear();
		declaredFieldsCache.clear();
	}

	/**
	 * Action to take on each method.
	 */
	@FunctionalInterface
	public interface MethodCallback {
		/**
		 * 使用指定方法完成一些操作.
		 * Perform an operation using the given method.
		 * @param method the method to operate on
		 */
		void doWith(Method method) throws IllegalArgumentException, IllegalAccessException;
	}

	/**
	 * Callback optionally used to filter methods to be operated on by a method callback.
	 */
	@FunctionalInterface
	public interface MethodFilter {
		/**
		 * 检查一个指定的方法是否匹配规则
		 * Determine whether the given method matches.
		 * @param method the method to check
		 */
		boolean matches(Method method);
	}

	/**
	 * Callback interface invoked on each field in the hierarchy.
	 */
	@FunctionalInterface
	public interface FieldCallback {
		/**
		 * Perform an operation using the given field.
		 * @param field the field to operate on
		 */
		void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
	}

	/**
	 * Callback optionally used to filter fields to be operated on by a field callback.
	 */
	@FunctionalInterface
	public interface FieldFilter {
		/**
		 * Determine whether the given field matches.
		 * @param field the field to check
		 */
		boolean matches(Field field);
	}

}
