

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 *
 * Interface to be implemented by beans that wish to be aware of their owning {@link BeanFactory}.
 * For example, beans can look up collaborating beans via the factory (Dependency Lookup).
 * Note that most beans will choose to receive references to collaborating beans via corresponding bean properties or constructor arguments (Dependency Injection).
 * 实现接口的beans 希望感知他们的 BeanFactory
 * 例如，bean可以通过工厂查找协作bean（依赖项查找）。
 * 注意，大多数bean将选择通过相应的bean属性或构造函数参数（依赖注入）来接收对协作bean的引用。
 * sos 理解：beanFactory 可以让你不通过 依赖注入的方式，也能够随意的读取IOC容器里面的对象
 * For a list of all bean lifecycle methods, see the {@link BeanFactory BeanFactory javadocs}.
 * @since 11.03.2003
 * @see BeanNameAware
 * @see BeanClassLoaderAware
 * @see InitializingBean
 * @see org.springframework.context.ApplicationContextAware
 */
public interface BeanFactoryAware extends Aware {

	/**
	 * Callback that supplies the owning factory to a bean instance.
	 * Invoked after the population of normal bean properties but before an initialization callback such as {@link InitializingBean#afterPropertiesSet()} or a custom init-method.
	 * 向bean实例提供所属工厂的回调。
	 * @param beanFactory owning BeanFactory (never null).The bean can immediately call methods on the factory.
	 * @throws BeansException in case of initialization errors
	 * @see BeanInitializationException
	 */
	void setBeanFactory(BeanFactory beanFactory) throws BeansException;
}
