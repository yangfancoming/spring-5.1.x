

package org.springframework.aop.support;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Pointcut and method matcher for use in simple <b>cflow</b>-style pointcut.
 * Note that evaluating such pointcuts is 10-15 times slower than evaluating
 * normal pointcuts, but they are useful in some cases.
 */
@SuppressWarnings("serial")
public class ControlFlowPointcut implements Pointcut, ClassFilter, MethodMatcher, Serializable {

	private Class<?> clazz;

	@Nullable
	private String methodName;

	private volatile int evaluations;

	/**
	 * Construct a new pointcut that matches all control flows below that class.
	 * @param clazz the clazz
	 */
	public ControlFlowPointcut(Class<?> clazz) {
		this(clazz, null);
	}

	/**
	 * Construct a new pointcut that matches all calls below the given method
	 * in the given class. If no method name is given, matches all control flows
	 * below the given class.
	 * @param clazz the clazz
	 * @param methodName the name of the method (may be {@code null})
	 */
	public ControlFlowPointcut(Class<?> clazz, @Nullable String methodName) {
		Assert.notNull(clazz, "Class must not be null");
		this.clazz = clazz;
		this.methodName = methodName;
	}


	/**
	 * Subclasses can override this for greater filtering (and performance).
	 */
	@Override
	public boolean matches(Class<?> clazz) {
		return true;
	}

	/**
	 * Subclasses can override this if it's possible to filter out some candidate classes.
	 */
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		return true;
	}

	@Override
	public boolean isRuntime() {
		return true;
	}

	@Override
	public boolean matches(Method method, Class<?> targetClass, Object... args) {
		this.evaluations++;

		for (StackTraceElement element : new Throwable().getStackTrace()) {
			if (element.getClassName().equals(this.clazz.getName()) &&
					(this.methodName == null || element.getMethodName().equals(this.methodName))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * It's useful to know how many times we've fired, for optimization.
	 */
	public int getEvaluations() {
		return this.evaluations;
	}


	@Override
	public ClassFilter getClassFilter() {
		return this;
	}

	@Override
	public MethodMatcher getMethodMatcher() {
		return this;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ControlFlowPointcut)) {
			return false;
		}
		ControlFlowPointcut that = (ControlFlowPointcut) other;
		return (this.clazz.equals(that.clazz)) && ObjectUtils.nullSafeEquals(this.methodName, that.methodName);
	}

	@Override
	public int hashCode() {
		int code = this.clazz.hashCode();
		if (this.methodName != null) {
			code = 37 * code + this.methodName.hashCode();
		}
		return code;
	}

}
