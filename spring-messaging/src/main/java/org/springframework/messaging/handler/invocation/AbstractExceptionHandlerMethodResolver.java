

package org.springframework.messaging.handler.invocation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ExceptionDepthComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

/**
 * Cache exception handling method mappings and provide options to look up a method
 * that should handle an exception. If multiple methods match, they are sorted using
 * {@link ExceptionDepthComparator} and the top match is returned.
 *
 *

 * @since 4.0
 */
public abstract class AbstractExceptionHandlerMethodResolver {

	private final Map<Class<? extends Throwable>, Method> mappedMethods = new HashMap<>(16);

	private final Map<Class<? extends Throwable>, Method> exceptionLookupCache = new ConcurrentReferenceHashMap<>(16);


	/**
	 * Protected constructor accepting exception-to-method mappings.
	 */
	protected AbstractExceptionHandlerMethodResolver(Map<Class<? extends Throwable>, Method> mappedMethods) {
		Assert.notNull(mappedMethods, "Mapped Methods must not be null");
		this.mappedMethods.putAll(mappedMethods);
	}

	/**
	 * Extract the exceptions this method handles.This implementation looks for
	 * sub-classes of Throwable in the method signature.
	 * The method is static to ensure safe use from sub-class constructors.
	 */
	@SuppressWarnings("unchecked")
	protected static List<Class<? extends Throwable>> getExceptionsFromMethodSignature(Method method) {
		List<Class<? extends Throwable>> result = new ArrayList<>();
		for (Class<?> paramType : method.getParameterTypes()) {
			if (Throwable.class.isAssignableFrom(paramType)) {
				result.add((Class<? extends Throwable>) paramType);
			}
		}
		if (result.isEmpty()) {
			throw new IllegalStateException("No exception types mapped to " + method);
		}
		return result;
	}


	/**
	 * Whether the contained type has any exception mappings.
	 */
	public boolean hasExceptionMappings() {
		return !this.mappedMethods.isEmpty();
	}

	/**
	 * Find a {@link Method} to handle the given exception.
	 * Use {@link ExceptionDepthComparator} if more than one match is found.
	 * @param exception the exception
	 * @return a Method to handle the exception, or {@code null} if none found
	 */
	@Nullable
	public Method resolveMethod(Exception exception) {
		Method method = resolveMethodByExceptionType(exception.getClass());
		if (method == null) {
			Throwable cause = exception.getCause();
			if (cause != null) {
				method = resolveMethodByExceptionType(cause.getClass());
			}
		}
		return method;
	}

	/**
	 * Find a {@link Method} to handle the given exception type. This can be
	 * useful if an {@link Exception} instance is not available (e.g. for tools).
	 * @param exceptionType the exception type
	 * @return a Method to handle the exception, or {@code null} if none found
	 * @since 4.3.1
	 */
	@Nullable
	public Method resolveMethodByExceptionType(Class<? extends Throwable> exceptionType) {
		Method method = this.exceptionLookupCache.get(exceptionType);
		if (method == null) {
			method = getMappedMethod(exceptionType);
			this.exceptionLookupCache.put(exceptionType, method);
		}
		return method;
	}

	/**
	 * Return the {@link Method} mapped to the given exception type, or {@code null} if none.
	 */
	@Nullable
	private Method getMappedMethod(Class<? extends Throwable> exceptionType) {
		List<Class<? extends Throwable>> matches = new ArrayList<>();
		for (Class<? extends Throwable> mappedException : this.mappedMethods.keySet()) {
			if (mappedException.isAssignableFrom(exceptionType)) {
				matches.add(mappedException);
			}
		}
		if (!matches.isEmpty()) {
			matches.sort(new ExceptionDepthComparator(exceptionType));
			return this.mappedMethods.get(matches.get(0));
		}
		else {
			return null;
		}
	}

}
