

package org.springframework.transaction.interceptor;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ObjectUtils;

/**
 * Inner class that implements a Pointcut that matches if the underlying
 * {@link TransactionAttributeSource} has an attribute for a given method.
 * @since 2.5.5
 */
@SuppressWarnings("serial")
abstract class TransactionAttributeSourcePointcut extends StaticMethodMatcherPointcut implements Serializable {

	//  我们先来看一下切点的判定，spring通过切点的matches方法来判断是否需要对目标对象进行代理:
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		// 如果目标类不为空，并且是已经使用Transaction环绕后生成的类，则会将其过滤掉
		if (TransactionalProxy.class.isAssignableFrom(targetClass) || PlatformTransactionManager.class.isAssignableFrom(targetClass) || PersistenceExceptionTranslator.class.isAssignableFrom(targetClass)) {
			return false;
		}
		// 获取TransactionAttributeSource对象，这个方法也就是上面一个代码片段中实现的方法，
		// 也就是说这个方法将返回AnnotationTransactionAttributeSource
		TransactionAttributeSource tas = getTransactionAttributeSource();
		// 通过TransactionAttributeSource获取事务属性配置，如果当前方法没有配置事务，则不对其进行环绕
		return (tas == null || tas.getTransactionAttribute(method, targetClass) != null);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TransactionAttributeSourcePointcut)) {
			return false;
		}
		TransactionAttributeSourcePointcut otherPc = (TransactionAttributeSourcePointcut) other;
		return ObjectUtils.nullSafeEquals(getTransactionAttributeSource(), otherPc.getTransactionAttributeSource());
	}

	@Override
	public int hashCode() {
		return TransactionAttributeSourcePointcut.class.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + getTransactionAttributeSource();
	}

	/**
	 * Obtain the underlying TransactionAttributeSource (may be {@code null}).
	 * To be implemented by subclasses.
	 */
	@Nullable
	protected abstract TransactionAttributeSource getTransactionAttributeSource();

}
