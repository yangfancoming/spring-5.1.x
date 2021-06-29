

package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.lang.Nullable;

/**
 * 用于从指定方法（普通方法、构造函数方法）中提取方法参数名称。
 * Interface to discover parameter names for methods and constructors.
 * Parameter name discovery is not always possible, but various strategies are available to try,
 * such as looking for debug information that may have been emitted at compile time,
 * and looking for argname annotation values optionally accompanying AspectJ annotated methods.
 * @since 2.0
 */
public interface ParameterNameDiscoverer {

	/**
	 * 从指定方法中获取参数名称列表
	 * Return parameter names for a method, or {@code null} if they cannot be determined.
	 * Individual entries in the array may be {@code null} if parameter names are only
	 * available for some parameters of the given method but not for others. However,
	 * it is recommended to use stub parameter names instead wherever feasible.
	 * @param method the method to find parameter names for
	 * @return an array of parameter names if the names can be resolved, or {@code null} if they cannot
	 */
	@Nullable
	String[] getParameterNames(Method method);

	/**
	 * 从指定构造方法中获取参数名称列表
	 * eg： public Person(String name, int age, String sex) 返回 paramNames = [name, age, sex]
	 * Return parameter names for a constructor, or {@code null} if they cannot be determined.
	 * Individual entries in the array may be {@code null} if parameter names are only
	 * available for some parameters of the given constructor but not for others. However,
	 * it is recommended to use stub parameter names instead wherever feasible.
	 * @param ctor the constructor to find parameter names for
	 * @return an array of parameter names if the names can be resolved,or {@code null} if they cannot
	 * @see org.springframework.beans.factory.support.BeanFactoryGenericsTests#testParameterNameDiscoverer() 【测试用例】
	 */
	@Nullable
	String[] getParameterNames(Constructor<?> ctor);
}
