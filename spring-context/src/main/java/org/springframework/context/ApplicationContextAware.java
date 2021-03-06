

package org.springframework.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;

/**
 * Interface to be implemented by any object that wishes to be notified of the {@link ApplicationContext} that it runs in.
 *
 * Implementing this interface makes sense for example when an object requires access to a set of collaborating beans.
 * Note that configuration via bean references is preferable to implementing this interface just for bean lookup purposes.
 *
 * This interface can also be implemented if an object needs access to file resources,
 * i.e. wants to call {@code getResource}, wants to publish an application event, or requires access to the MessageSource.
 * However, it is preferable to implement the more specific {@link ResourceLoaderAware},
 * {@link ApplicationEventPublisherAware} or {@link MessageSourceAware} interface in such a specific scenario.
 *
 * Note that file resource dependencies can also be exposed as bean properties of type {@link org.springframework.core.io.Resource},
 * populated via Strings with automatic type conversion by the bean factory.
 * This removes the need for implementing any callback interface just for the purpose of accessing a specific file resource.
 * {@link org.springframework.context.support.ApplicationObjectSupport} is a convenience base class for application objects, implementing this interface.
 * For a list of all bean lifecycle methods, see the {@link org.springframework.beans.factory.BeanFactory BeanFactory javadocs}.
 * 例如，当一个对象需要访问一组协作bean时，实现这个接口是有意义的。
 * 但是要注意，如果仅仅是为了bean查找， 通过bean引用进行配置的方式要比实现这个接口更好。
 * @see ResourceLoaderAware
 * @see ApplicationEventPublisherAware
 * @see MessageSourceAware
 * @see org.springframework.context.support.ApplicationObjectSupport
 * @see org.springframework.beans.factory.BeanFactoryAware
 */
public interface ApplicationContextAware extends Aware {

	/**
	 * Set the ApplicationContext that this object runs in.
	 * Normally this call will be used to initialize the object.
	 * Invoked after population of normal bean properties but before an init callback such as {@link org.springframework.beans.factory.InitializingBean#afterPropertiesSet()}
	 * or a custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
	 * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and {@link MessageSourceAware}, if applicable.
	 * @param applicationContext the ApplicationContext object to be used by this object
	 * @throws ApplicationContextException in case of context initialization errors
	 * @throws BeansException if thrown by application context methods
	 * @see org.springframework.beans.factory.BeanInitializationException
	 */
	void setApplicationContext(ApplicationContext applicationContext) throws BeansException;

}
