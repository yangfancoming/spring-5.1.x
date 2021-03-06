

package org.springframework.beans.factory;

import org.springframework.lang.Nullable;

/**
 * Interface to be implemented by objects used within a {@link BeanFactory} which are themselves factories for individual objects. 
 * If a bean implements this interface, it is used as a factory for an object to expose, not directly as a bean instance that will be exposed itself.
 * 
 * NB: A bean that implements this interface cannot be used as a normal bean.
 * A FactoryBean is defined in a bean style, but the object exposed for bean references ({@link #getObject()}) is always the object that it creates.
 *
 * FactoryBeans can support singletons and prototypes, and can either create objects lazily on demand or eagerly on startup.
 *  The {@link SmartFactoryBean} interface allows for exposing more fine-grained behavioral metadata.
 *
 * This interface is heavily used within the framework itself, for example for
 * the AOP {@link org.springframework.aop.framework.ProxyFactoryBean} or the {@link org.springframework.jndi.JndiObjectFactoryBean}.
 *
 * It can be used for custom components as well; however, this is only common for infrastructure code.
 *
 * {@code FactoryBean} is a programmatic contract. Implementations are not supposed to rely on annotation-driven injection or other reflective facilities.
 *
 * {@link #getObjectType()} {@link #getObject()} invocations may arrive early in the bootstrap process, even ahead of any post-processor setup.
 * If you need access other beans, implement {@link BeanFactoryAware} and obtain them programmatically.
 *
 * Finally, FactoryBean objects participate in the containing BeanFactory's synchronization of bean creation.
 * There is usually no need for internal synchronization other than for purposes of lazy initialization within the FactoryBean itself (or the like).
 *
 * @since 08.03.2003
 * @param <T> the bean type
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @see org.springframework.jndi.JndiObjectFactoryBean
 *
 * 第三方框架要继承进Spring，往往就是通过实现FactoryBean来集成的。
 * 比如MyBatis的SqlSessionFactoryBean、RedisRepositoryFactoryBean、EhCacheManagerFactoryBean等等
 */
public interface FactoryBean<T> {

	/**
	 * 返回的对象实例 //从工厂中获取bean【这个方法是FactoryBean的核心】
	 * Return an instance (possibly shared or independent) of the object managed by this factory.
	 * As with a {@link BeanFactory}, this allows support for both the Singleton and Prototype design pattern.
	 * If this FactoryBean is not fully initialized yet at the time of the call (for example because it is involved in a circular reference),
	 * throw a corresponding {@link FactoryBeanNotInitializedException}.
	 * As of Spring 2.0, FactoryBeans are allowed to return {@code null} objects.
	 * The factory will consider this as normal value to be used;
	 * it will not throw a FactoryBeanNotInitializedException in this case anymore.
	 * FactoryBean implementations are encouraged to throw FactoryBeanNotInitializedException themselves now, as appropriate.
	 * @return an instance of the bean (can be {@code null})
	 * @throws Exception in case of creation errors
	 * @see FactoryBeanNotInitializedException
	 */
	@Nullable
	T getObject() throws Exception;

	/**
	 * 返回Bean的类型 //获取Bean工厂创建的对象的类型【注意这个方法主要作用是：该方法返回的类型是在ioc容器中getbean所匹配的类型】
	 * Return the type of object that this FactoryBean creates,or {@code null} if not known in advance.
	 * This allows one to check for specific types of beans without  instantiating objects, for example on autowiring.
	 * In the case of implementations that are creating a singleton object,
	 * this method should try to avoid singleton creation as far as possible; it should rather estimate the type in advance.
	 * For prototypes, returning a meaningful type here is advisable too.
	 * This method can be called <i>before</i> this FactoryBean has been fully initialized.
	 * It must not rely on state created during initialization; of course, it can still use such state if available.
	 * NOTE: Autowiring will simply ignore FactoryBeans that return @code null} here.
	 * {Therefore it is highly recommended to implement this method properly, using the current state of the FactoryBean.
	 * @return the type of object that this FactoryBean creates,or {@code null} if not known at the time of the call
	 * @see ListableBeanFactory#getBeansOfType
	 */
	@Nullable
	Class<?> getObjectType();

	/**
	 * true是单例，false是非单例  在Spring5.0中此方法利用了JDK1.8的新特性变成了default方法，返回true
	 * Is the object managed by this factory a singleton? That is,will {@link #getObject()} always return the same object (a reference that can be cached)?
	 * NOTE: If a FactoryBean indicates to hold a singleton object,the object returned from {@code getObject()} might get cached by the owning BeanFactory.
	 * Hence, do not return {@code true} unless the FactoryBean always exposes the same reference.
	 * The singleton status of the FactoryBean itself will generally
	 * be provided by the owning BeanFactory; usually, it has to be defined as singleton there.
	 * NOTE: This method returning {@code false} does not necessarily indicate that returned objects are independent instances.
	 * An implementation of the extended {@link SmartFactoryBean} interface may explicitly indicate independent instances through its
	 * {@link SmartFactoryBean#isPrototype()} method. Plain {@link FactoryBean}
	 * implementations which do not implement this extended interface are simply assumed to always return independent instances if the
	 * {@code isSingleton()} implementation returns {@code false}.
	 * The default implementation returns {@code true}, since a {@code FactoryBean} typically manages a singleton instance.
	 * @return whether the exposed object is a singleton
	 * @see #getObject()
	 * @see SmartFactoryBean#isPrototype()
	 */
	default boolean isSingleton() {
		return true;
	}
}
