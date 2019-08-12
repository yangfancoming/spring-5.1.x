

package org.springframework.beans.factory;

/**
 * Interface to be implemented by beans that need to react once all their properties have been set by a {@link BeanFactory}:
 * 由bean实现的接口，这些bean的所有属性都由beanfactory设置后需要作出反应。
 *  e.g. to perform custom initialization, or merely to check that all mandatory properties have been set.
 *
 * <p>An alternative to implementing {@code InitializingBean} is specifying a custom init method,
 * for example in an XML bean definition. For a list of all bean lifecycle methods, see the {@link BeanFactory BeanFactory javadocs}.

 * @see DisposableBean
 * @see org.springframework.beans.factory.config.BeanDefinition#getPropertyValues()
 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#getInitMethodName()
 *
 * 在spring初始化bean的时候，如果bean实现了 InitializingBean 接口，会自动调用 afterPropertiesSet 方法
 */
public interface InitializingBean {

	/**
	 * Invoked by the containing {@code BeanFactory} after it has set all bean properties and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
	 * <p>This method allows the bean instance to perform validation of its overall configuration and final initialization when all bean properties have been set.
	 * @throws Exception in the event of misconfiguration (such as failure to set an essential property) or if initialization fails for any other reason
	 */
	void afterPropertiesSet() throws Exception;

}
