

package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.lang.Nullable;

/**
 * A BeanDefinition describes a bean instance, which has property values,constructor argument values, and further information supplied by concrete implementations.
 * beandefinition 描述一个bean实例，该实例具有属性值、构造函数参数值和具体实现提供的进一步信息
 *
 * This is just a minimal interface: The main intention is to allow a
 * {@link BeanFactoryPostProcessor} such as {@link PropertyPlaceholderConfigurer} to introspect and modify property values and other bean metadata.

 * @since 19.03.2004
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 *
 * BeanDefinition 是一个接口，它是配置文件 <bean> 标签在 Spring 容器中的内部表现形式，<bean> 标签拥有的属性也会对应于 BeanDefinition 中的属性，
 * 它们是一一对应的，即一个 <bean> 标签对应于一个 BeanDefinition 实例
 *
 * 共有三个实现类，在配置文件中可以有父bean和子bean，父bean用 RootBeanDefinition 来表示，子bean用 ChildBeanDefinition 来表示，而 GenericBeanDefinition 是一个通用的BeanDefinition。
 * 存储  内存态的bean 好比是设计图纸  根据设计图纸 来创建  纯静态的bean
 *
 * Bean 的生命周期，默认只提供sington和prototype两种，在WebApplicationContext中还会有request, session, globalSession, application, websocket 等
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	/**
	 * Scope identifier for the standard singleton scope: "singleton".
	 * Note that extended bean factories might support further scopes.
	 * @see #setScope
	 */
	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

	/**
	 * Scope identifier for the standard prototype scope: "prototype".
	 * Note that extended bean factories might support further scopes.
	 * @see #setScope
	 */
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

	/**
	 * Role hint indicating that a {@code BeanDefinition} is a major part of the application.
	 * Typically corresponds to a user-defined bean.
	 */
	int ROLE_APPLICATION = 0;

	/**
	 * Role hint indicating that a {@code BeanDefinition} is a supporting part of some larger configuration, typically an outer
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 * {@code SUPPORT} beans are considered important enough to be aware of when looking more closely at a particular
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition},but not when looking at the overall configuration of an application.
	 */
	int ROLE_SUPPORT = 1;

	/**
	 * Role hint indicating that a {@code BeanDefinition} is providing an entirely background role and has no relevance to the end-user.
	 * This hint is used when registering beans that are completely part of the internal workings
	 * of a {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 */
	int ROLE_INFRASTRUCTURE = 2;


	// Modifiable attributes

	// Set the name of the parent definition of this bean definition, if any. 一句话就是：继承父 Bean 的配置信息而已
	void setParentName(@Nullable String parentName);

	//  Return the name of the parent definition of this bean definition, if any.
	@Nullable
	String getParentName();

	/**
	 * Specify the bean class name of this bean definition.
	 * The class name can be modified during bean factory post-processing,
	 * typically replacing the original class name with a parsed variant of it.
	 * @see #setParentName
	 * @see #setFactoryBeanName
	 * @see #setFactoryMethodName
	 */
	void setBeanClassName(@Nullable String beanClassName);

	/**
	 * Return the current bean class name of this bean definition.
	 * Note that this does not have to be the actual class name used at runtime, in
	 * case of a child definition overriding/inheriting the class name from its parent.
	 * Also, this may just be the class that a factory method is called on, or it may
	 * even be empty in case of a factory bean reference that a method is called on.
	 * Hence, do <i>not</i> consider this to be the definitive bean type at runtime but
	 * rather only use it for parsing purposes at the individual bean definition level.
	 * @see #getParentName()
	 * @see #getFactoryBeanName()
	 * @see #getFactoryMethodName()
	 */
	@Nullable
	String getBeanClassName();

	/**
	 * Override the target scope of this bean, specifying a new scope name.
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 */
	void setScope(@Nullable String scope);

	// Return the name of the current target scope for this bean, or {@code null} if not known yet.
	@Nullable
	String getScope();

	/**
	 * Set whether this bean should be lazily initialized.
	 * If {@code false}, the bean will get instantiated on startup by bean factories that perform eager initialization of singletons.
	 */
	void setLazyInit(boolean lazyInit);

	// Return whether this bean should be lazily initialized, i.e. not eagerly instantiated on startup. Only applicable to a singleton bean.
	boolean isLazyInit();

	/**
	 * Set the names of the beans that this bean depends on being initialized.
	 * The bean factory will guarantee that these beans get initialized first.
	 * 设置该Bean依赖的所有Bean 注意，这里的依赖不是指属性依赖(如 @Autowire 标记的)，是 depends-on="" 属性设置的值。
	 */
	void setDependsOn(@Nullable String... dependsOn);

	// Return the bean names that this bean depends on. 返回该 Bean 的所有依赖
	@Nullable
	String[] getDependsOn();

	/**
	 * Set whether this bean is a candidate for getting autowired into some other bean.
	 * Note that this flag is designed to only affect type-based autowiring.
	 * It does not affect explicit references by name, which will get resolved even
	 * if the specified bean is not marked as an autowire candidate.
	 * As a consequence,autowiring by name will nevertheless inject a bean if the name matches.
	 * 设置该Bean是否可以注入到其他Bean中 只对根据类型注入有效，如果根据名称注入，即使这边设置了 false，也是可以的
	 */
	void setAutowireCandidate(boolean autowireCandidate);

	// Return whether this bean is a candidate for getting autowired into some other bean. 该Bean是否可以注入到其他Bean中
	boolean isAutowireCandidate();

	/**
	 * Set whether this bean is a primary autowire candidate.
	 * If this value is {@code true} for exactly one bean among multiple matching candidates, it will serve as a tie-breaker.
	 *  同一接口的多个实现，如果不指定名字的话，Spring会优先选择设置primary为true的bean
	 */
	void setPrimary(boolean primary);

	// Return whether this bean is a primary autowire candidate.
	boolean isPrimary();

	/**
	 * Specify the factory bean to use, if any.
	 * This the name of the bean to call the specified factory method on.
	 * @see #setFactoryMethodName
	 *   如果该 Bean 采用工厂方法生成，指定工厂名称。对工厂不熟悉的读者，请参加附录
	 *   一句话就是：有些实例不是用反射生成的，而是用工厂模式生成的
	 */
	void setFactoryBeanName(@Nullable String factoryBeanName);

	//  Return the factory bean name, if any.
	@Nullable
	String getFactoryBeanName();

	/**
	 * Specify a factory method, if any. This method will be invoked with
	 * constructor arguments, or with no arguments if none are specified.
	 * The method will be invoked on the specified factory bean, if any,or otherwise as a static method on the local bean class.
	 * @see #setFactoryBeanName
	 * @see #setBeanClassName
	 *  指定工厂类中的 工厂方法名称
	 */
	void setFactoryMethodName(@Nullable String factoryMethodName);

	//  Return a factory method, if any.
	@Nullable
	String getFactoryMethodName();

	/**
	 * Return the constructor argument values for this bean.
	 * The returned instance can be modified during bean factory post-processing.
	 * @return the ConstructorArgumentValues object (never {@code null})  构造器参数
	 */
	ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * Return if there are constructor argument values defined for this bean.
	 * @since 5.0.2
	 */
	default boolean hasConstructorArgumentValues() {
		return !getConstructorArgumentValues().isEmpty();
	}

	/**
	 * Return the property values to be applied to a new instance of the bean.
	 * The returned instance can be modified during bean factory post-processing.
	 * @return the MutablePropertyValues object (never {@code null})
	 * Bean 中的属性值，后面给 bean 注入属性值的时候会说到
	 */
	MutablePropertyValues getPropertyValues();

	//  Return if there are property values values defined for this bean.  @since 5.0.2
	default boolean hasPropertyValues() {
		return !getPropertyValues().isEmpty();
	}

	// Set the name of the initializer method.  @since 5.1
	void setInitMethodName(@Nullable String initMethodName);

	//  Return the name of the initializer method. @since 5.1
	@Nullable
	String getInitMethodName();

	// Set the name of the destroy method.  @since 5.1
	void setDestroyMethodName(@Nullable String destroyMethodName);

	// Return the name of the destroy method.  @since 5.1
	@Nullable
	String getDestroyMethodName();

	/**
	 * Set the role hint for this {@code BeanDefinition}. The role hint
	 * provides the frameworks as well as tools with an indication of
	 * the role and importance of a particular {@code BeanDefinition}.
	 * @since 5.1
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 */
	void setRole(int role);

	/**
	 * Get the role hint for this {@code BeanDefinition}. The role hint
	 * provides the frameworks as well as tools with an indication of
	 * the role and importance of a particular {@code BeanDefinition}.
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_SUPPORT
	 * @see #ROLE_INFRASTRUCTURE
	 */
	int getRole();

	// Set a human-readable description of this bean definition. @since 5.1
	void setDescription(@Nullable String description);

	// Return a human-readable description of this bean definition.
	@Nullable
	String getDescription();


	// Read-only attributes

	/**
	 * Return whether this a <b>Singleton</b>, with a single, shared instance returned on all calls.
	 * @see #SCOPE_SINGLETON
	 */
	boolean isSingleton();

	/**
	 * Return whether this a <b>Prototype</b>, with an independent instance returned for each call.
	 * @since 3.0
	 * @see #SCOPE_PROTOTYPE
	 */
	boolean isPrototype();

	/**
	 * Return whether this bean is "abstract", that is, not meant to be instantiated.
	 * 如果这个 Bean 是被设置为 abstract，那么不能实例化，常用于作为 父bean 用于继承
	 */
	boolean isAbstract();

	// Return a description of the resource that this bean definition came from (for the purpose of showing context in case of errors).
	@Nullable
	String getResourceDescription();

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * Allows for retrieving the decorated bean definition, if any.
	 * Note that this method returns the immediate originator. Iterate through the
	 * originator chain to find the original BeanDefinition as defined by the user.
	 */
	@Nullable
	BeanDefinition getOriginatingBeanDefinition();

}
