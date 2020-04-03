

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * Defines a factory which can return an Object instance (possibly shared or independent) when invoked.
 * This interface is typically used to encapsulate a generic factory which returns a new instance (prototype) of some target object on each invocation.
 * This interface is similar to {@link FactoryBean}, but implementations of the latter are normally meant to be defined as SPI instances in a
 * {@link BeanFactory}, while implementations of this class are normally meant to be fed as an API to other beans (through injection). As such, the
 * {@code getObject()} method has different exception handling behavior.
 * @since 1.0.2
 * @param <T> the object type
 * @see FactoryBean
 * 它可以将创建对象的步骤封装到ObjectFactory中 交给自定义的Scope来选择是否需要创建对象来灵活的实现scope。  具体参见Scope接口
 */
@FunctionalInterface
public interface ObjectFactory<T> {

	/**
	 * Return an instance (possibly shared or independent) of the object managed by this factory.
	 * @return the resulting instance
	 * @throws BeansException in case of creation errors
	 */
	T getObject() throws BeansException;

}
