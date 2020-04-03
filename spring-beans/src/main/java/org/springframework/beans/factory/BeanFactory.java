

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * The root interface for accessing a Spring bean container.
 * This is the basic client view of a bean container;
 * further interfaces such as ListableBeanFactory and ConfigurableBeanFactory are available for specific purposes.
 *
 * This interface is implemented by objects that hold a number of bean definitions,each uniquely identified by a String name.
 * Depending on the bean definition,the factory will return either an independent instance of a contained object (the Prototype design pattern),
 * or a single shared instance (a superior alternative to the Singleton design pattern, in which the instance is a singleton in the scope of the factory).
 * Which type of instance will be returned depends on the bean factory configuration: the API is the same.
 * Since Spring 2.0, further scopes are available depending on the concrete application context (e.g. "request" and "session" scopes in a web environment).
 *
 * The point of this approach is that the BeanFactory is a central registry of application components,
 *  and centralizes configuration of application components  (no more do individual objects need to read properties files,for example).
 *  See chapters 4 and 11 of "Expert One-on-One J2EE Design and Development" for a discussion of the benefits of this approach.
 *
 * Note that it is generally better to rely on Dependency Injection ("push" configuration) to configure application objects through setters or constructors,
 * rather than use any form of "pull" configuration like a BeanFactory lookup.
 * Spring's Dependency Injection functionality is implemented using this BeanFactory interface and its subinterfaces.
 *
 * Normally a BeanFactory will load bean definitions stored in a configuration source (such as an XML document),
 *  and use the {@code org.springframework.beans} package to configure the beans.
 * However, an implementation could simply return Java objects it creates as necessary directly in Java code.
 * There are no constraints on how the definitions could be stored: LDAP, RDBMS, XML,properties file, etc.
 * Implementations are encouraged to support references amongst beans (Dependency Injection).
 *
 * In contrast to the methods in {@link ListableBeanFactory}, all of the operations in this interface
 * will also check parent factories if this is a {@link HierarchicalBeanFactory}.
 * If a bean is not found in this factory instance,the immediate parent factory will be asked.
 * Beans in this factory instance are supposed to override beans of the same name in any parent factory.
 *
 * Bean factory implementations should support the standard bean lifecycle interfaces as far as possible. 
 * The full set of initialization methods and their standard order is:
 * 
 * BeanNameAware's {@code setBeanName}
 * BeanClassLoaderAware's {@code setBeanClassLoader}
 * BeanFactoryAware's {@code setBeanFactory}
 * EnvironmentAware's {@code setEnvironment}
 * EmbeddedValueResolverAware's {@code setEmbeddedValueResolver}
 * ResourceLoaderAware's {@code setResourceLoader}
 * (only applicable when running in an application context)
 * ApplicationEventPublisherAware's {@code setApplicationEventPublisher}
 * (only applicable when running in an application context)
 * MessageSourceAware's {@code setMessageSource}
 * (only applicable when running in an application context)
 * ApplicationContextAware's {@code setApplicationContext}
 * (only applicable when running in an application context)
 * ServletContextAware's {@code setServletContext}
 * (only applicable when running in a web application context)
 * {@code postProcessBeforeInitialization} methods of BeanPostProcessors
 * InitializingBean's {@code afterPropertiesSet} a custom init-method definition
 * {@code postProcessAfterInitialization} methods of BeanPostProcessors
 *
 * On shutdown of a bean factory, the following lifecycle methods apply:
 * {@code postProcessBeforeDestruction} methods of DestructionAwareBeanPostProcessors
 * DisposableBean's {@code destroy} a custom destroy-method definition
 *
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 *
 * 定义了获取bean、及获取bean的各种属性
 * BeanFactory 是访问 Spring 容器的根接口，定义了获取bean的各种重载方法：
 */
public interface BeanFactory {

	/**
	 * Used to dereference a {@link FactoryBean} instance and distinguish it from beans created by the FactoryBean.
	 * 用于取消对FactoryBean实例的引用，并将其与FactoryBean创建的bean区分开来
	 * For example, if the bean named {@code myJndiObject} is a FactoryBean, getting {@code &myJndiObject} will return the factory, not the instance returned by the factory.
	 * 这是用来区分是获取FactoryBean还是FactoryBean的createBean创建的实例.如果&开始则获取FactoryBean;否则获取createBean创建的实例.
	 */
	String FACTORY_BEAN_PREFIX = "&";

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * This method allows a Spring BeanFactory to be used as a replacement for the Singleton or Prototype design pattern.
	 * Callers may retain references to returned objects in the case of Singleton beans.
	 * Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to retrieve
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no bean with the specified name
	 * @throws BeansException if the bean could not be obtained
	 * 根据bean的名字，获取在IOC容器中得到bean实例
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * Behaves the same as {@link #getBean(String)}, but provides a measure of type
	 * safety by throwing a BeanNotOfRequiredTypeException if the bean is not of the required type.
	 * This means that ClassCastException can't be thrown on casting the result correctly, as can happen with {@link #getBean(String)}.
	 * Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to retrieve
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 * 根据bean的名字和Class类型来得到bean实例，增加了类型安全验证机制。
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * Allows for specifying explicit constructor arguments / factory method arguments,overriding the specified default arguments (if any) in the bean definition.
	 *
	 * @param name the name of the bean to retrieve  要获取bean的名称
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 2.5
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 * 根据给定对象类型，返回唯一匹配的bean实例（如果有的话）。
	 * This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name of the given type.
	 * For more extensive retrieval operations across sets of beans, use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 4.1
	 */
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	/**
	 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see #getBeanProvider(ResolvableType)
	 */
	<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

	/**
	 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
	 * of instances, including availability and uniqueness options.
	 * @param requiredType type the bean must match; can be a generic type declaration.
	 * Note that collection types are not supported here, in contrast to reflective
	 * injection points. For programmatically retrieving a list of beans matching a
	 * specific type, specify the actual bean type as an argument here and subsequently
	 * use {@link ObjectProvider#orderedStream()} or its lazy streaming/iteration options.
	 * @return a corresponding provider handle
	 * @since 5.1
	 * @see ObjectProvider#iterator()
	 * @see ObjectProvider#stream()
	 * @see ObjectProvider#orderedStream()
	 */
	<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);

	/**
	 * Does this bean factory contain a bean definition or externally registered singleton instance with the given name?
	 * If the given name is an alias, it will be translated back to the corresponding canonical bean name.
	 * If this factory is hierarchical, will ask any parent factory if the bean cannot be found in this factory instance.
	 * If a bean definition or singleton instance matching the given name is found,
	 * this method will return {@code true} whether the named bean definition is concrete
	 * or abstract, lazy or eager, in scope or not.
	 * Therefore, note that a {@code true} return value from this method does not necessarily indicate that {@link #getBean}
	 * will be able to obtain an instance for the same name.
	 * @param name the name of the bean to query
	 * @return whether a bean with the given name is present
	 * 提供对bean的检索，看看是否在IOC容器有这个名字的bean
	 * 判断是否包含Bean。此处有个陷阱：这边不管类是否抽象类,懒加载,是否在容器范围内,只要符合都返回true,所以这边true,**不一定能从getBean获取实例**
	 */
	boolean containsBean(String name);

	/**
	 * Is this bean a shared singleton? That is, will {@link #getBean} always return the same instance?
	 * Note: This method returning {@code false} does not clearly indicate independent instances.
	 * It indicates non-singleton instances, which may correspond to a scoped bean as well.
	 * Use the {@link #isPrototype} operation to explicitly check for independent instances.
	 * Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return whether this bean corresponds to a singleton instance
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @see #getBean
	 * @see #isPrototype
	 * 根据bean名字得到bean实例，并同时判断这个bean是不是单例
	 */
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Is this bean a prototype? That is, will {@link #getBean} always return independent instances?
	 * Note: This method returning {@code false} does not clearly indicate a singleton object.
	 * It indicates non-independent instances, which may correspond to a scoped bean as well.
	 * Use the {@link #isSingleton} operation to explicitly check for a shared singleton instance.
	 * Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return whether this bean will always deliver independent instances
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.3
	 * @see #getBean
	 * @see #isSingleton
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Check whether the bean with the given name matches the specified type.
	 * 检查具有给定名称的bean是否与指定类型匹配。
	 * More specifically, check whether a {@link #getBean} call for the given name would return an object that is assignable to the specified target type.
	 * Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query   要检测的bean名称
	 * @param typeToMatch the type to match against (as a {@code ResolvableType})  要匹配的类型
	 * @return {@code true} if the bean type matches, {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 4.2
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * Check whether the bean with the given name matches the specified type.
	 * More specifically, check whether a {@li nk #getBean} call for the given name
	 * would return an object that is assignable to the specified target type.
	 * Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code Class})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.1
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * Determine the type of the bean with the given name. More specifically,
	 * determine the type of object that {@link #getBean} would return for the given name.
	 * For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
	 * as exposed by {@link FactoryBean#getObjectType()}.
	 * Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return the type of the bean, or {@code null} if not determinable
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 1.1.2
	 * @see #getBean
	 * @see #isTypeMatch
	 * 得到bean实例的Class类型
	 */
	@Nullable
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Return the aliases for the given bean name, if any.
	 * All of those aliases point to the same bean when used in a {@link #getBean} call.
	 * If the given name is an alias, the corresponding original bean name and other aliases (if any) will be returned,
	 * with the original bean name being the first element in the array.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the bean name to check for aliases
	 * @return the aliases, or an empty array if none
	 * @see #getBean
	 * 得到bean的别名，如果根据别名检索，那么其原名也会被检索出来
	 */
	String[] getAliases(String name);

}
