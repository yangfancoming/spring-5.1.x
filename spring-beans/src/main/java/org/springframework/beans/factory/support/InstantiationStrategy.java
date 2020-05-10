

package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;

/**
 * Interface responsible for creating instances corresponding to a root bean definition.
 * This is pulled out into a strategy as various approaches are possible,including using CGLIB to create subclasses on the fly to support Method Injection.
 * @since 1.1
 * 简而言之，就是根据 RootBeanDefinition，去实例化一个实例（相当于new了一个对象而已，bean的具体的属性在此时并未赋值）
 * 下面是他的三个重载方法
 * 	owner：这个Bean定义所属的BeanFactory工厂
 * 	args：构造函数的参数（大多数情况下都用无参构造）
 * 	factoryBean:factoryMethod  也支持工厂方法方式创建实例（注意此factoryBean非我们所常指的接口：FactoryBean哦~）
 */
public interface InstantiationStrategy {

	/**
	 * Return an instance of the bean with the given name in this factory.
	 * @param bd the bean definition
	 * @param beanName the name of the bean when it is created in this context. The name can be {@code null} if we are autowiring a bean which doesn't belong to the factory.
	 * @param owner the owning BeanFactory
	 * @return a bean instance for this bean definition
	 * @throws BeansException if the instantiation attempt failed
	 */
	Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner) throws BeansException;

	/**
	 * Return an instance of the bean with the given name in this factory, creating it via the given constructor.
	 * @param bd the bean definition
	 * @param beanName the name of the bean when it is created in this context.The name can be {@code null} if we are autowiring a bean which doesn't belong to the factory.
	 * @param owner the owning BeanFactory
	 * @param ctor the constructor to use
	 * @param args the constructor arguments to apply
	 * @return a bean instance for this bean definition
	 * @throws BeansException if the instantiation attempt failed
	 */
	Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,Constructor<?> ctor, Object... args) throws BeansException;

	/**
	 * Return an instance of the bean with the given name in this factory,creating it via the given factory method.
	 * @param bd the bean definition
	 * @param beanName the name of the bean when it is created in this context.The name can be {@code null} if we are autowiring a bean which doesn't belong to the factory.
	 * @param owner the owning BeanFactory
	 * @param factoryBean the factory bean instance to call the factory method on, or {@code null} in case of a static factory method
	 * @param factoryMethod the factory method to use
	 * @param args the factory method arguments to apply
	 * @return a bean instance for this bean definition
	 * @throws BeansException if the instantiation attempt failed
	 */
	Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,@Nullable Object factoryBean, Method factoryMethod, Object... args) throws BeansException;

}
