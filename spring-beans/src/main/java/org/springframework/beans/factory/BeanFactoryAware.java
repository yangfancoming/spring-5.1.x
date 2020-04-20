

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * Interface to be implemented by beans that wish to be aware of their owning {@link BeanFactory}.
 * 实现接口的beans 希望感知他们的 BeanFactory
 * For example, beans can look up collaborating beans via the factory (Dependency Lookup).
 * 例如，bean可以通过工厂查找协作bean（依赖项查找）。
 * Note that most beans will choose to receive references to collaborating beans via corresponding bean properties or constructor arguments (Dependency Injection).
 * 注意，大多数bean将选择通过相应的bean属性或构造函数参数（依赖注入）来接收对协作bean的引用。
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
	 * 向bean实例提供所属工厂的回调。
	 * Invoked after the population of normal bean properties but before an initialization callback such as {@link InitializingBean#afterPropertiesSet()} or a custom init-method.
	 * @param beanFactory owning BeanFactory (never null).
	 * The bean can immediately call methods on the factory.
	 * @throws BeansException in case of initialization errors
	 * @see BeanInitializationException
	 */
	void setBeanFactory(BeanFactory beanFactory) throws BeansException;

}
