

package org.springframework.beans.factory.config;

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.lang.Nullable;

/**
 * Extension of the {@link org.springframework.beans.factory.BeanFactory} interface to be implemented by bean factories
 * that are capable of autowiring, provided that they want to expose this functionality for existing bean instances.
 *
 * This subinterface of BeanFactory is not meant to be used in normal application code:
 * stick to {@link org.springframework.beans.factory.BeanFactory} or {@link org.springframework.beans.factory.ListableBeanFactory} for typical use cases.
 *
 * Integration code for other frameworks can leverage this interface to wire and populate existing bean instances that Spring does not control the lifecycle of.
 * This is particularly useful for WebWork Actions and Tapestry Page objects, for example.
 * Note that this interface is not implemented by {@link org.springframework.context.ApplicationContext} facades,as it is hardly ever used by application code.
 * That said, it is available from an application context too, accessible through ApplicationContext's {@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()} method.
 * You may also implement the {@link org.springframework.beans.factory.BeanFactoryAware} interface,
 * which exposes the internal BeanFactory even when running in an  ApplicationContext, to get access to an AutowireCapableBeanFactory:
 * simply cast the passed-in BeanFactory to AutowireCapableBeanFactory.
 * @since 04.12.2003
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 *
 * 扩展了BeanFactory接口,并提供了自动装配能力
 *
 * AutowireCapableBeanFactory这个接口一般在applicationContext的内部是较少使用的，它的功能主要是为了装配applicationContext管理之外的Bean。
 * 所以，常用于第三方框架的集成，比如：mybatis，quartz等
 *
 * 从宏观上看，AutowireCapableBeanFactory提供了如下能力：
 * 1、为已经实例化的对象装配属性，这些属性对象都是Spring管理的；
 * 2、实例化一个类型，并自动装配，这些属性对象都是Spring管理的，实例化的类不被Spring管理。所以这个接口提供功能就是自动装配
 *
 * 提供创建bean，自动注入，初始化以及应用bean的后置处理器。
 * 扩展了BeanFactory，主要提供了自动装配能力
 *
 * 对于想要拥有自动装配能力，并且想把这种能力暴露给外部应用的BeanFactory类需要实现此接口。
 * 正常情况下，不要使用此接口，应该更倾向于使用BeanFactory或者ListableBeanFactory接口。
 * 此接口主要是针对框架之外，没有向Spring托管Bean的应用。通过暴露此功能，Spring框架之外的程序，具有自动装配等Spring的功能。
 * 需要注意的是，ApplicationContext接口并没有实现此接口，因为应用代码很少用到此功能，如果确实需要的话，可以调用 ApplicationContext 的getAutowireCapableBeanFactory方法，来获取此接口的实例。
 * 如果一个类实现了此接口，那么很大程度上它还需要实现BeanFactoryAware接口。它可以在应用上下文中返回BeanFactory。
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

	/**
	 * Constant that indicates no externally defined autowiring. Note that BeanFactoryAware etc and annotation-driven injection will still be applied.
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 * 无自动装配，常量，用于标识外部自动装配功能是否可用。但是此标识不影响正常的（基于注解的等）自动装配功能的使用
	 */
	int AUTOWIRE_NO = 0;

	/**
	 * Constant that indicates autowiring bean properties by name (applying to all bean property setters).
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 * 按 by-name 装配
	 */
	int AUTOWIRE_BY_NAME = 1;

	/**
	 * Constant that indicates autowiring bean properties by type (applying to all bean property setters).
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 * 按 by-type 装配
	 */
	int AUTOWIRE_BY_TYPE = 2;

	/**
	 * Constant that indicates autowiring the greediest constructor that can be satisfied (involves resolving the appropriate constructor).
	 * @see #createBean
	 * @see #autowire
	 * constructor构造函数装配，标识按照贪婪策略匹配出的最符合的构造方法来自动装配的常量
	 */
	int AUTOWIRE_CONSTRUCTOR = 3;

	/**
	 * Constant that indicates determining an appropriate autowire strategy through introspection of the bean class.
	 * @see #createBean
	 * @see #autowire
	 * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
	 * prefer annotation-based autowiring for clearer demarcation of autowiring needs.
	 * 自动装配,已被标记为过时，标识自动识别一种装配策略来实现自动装配的常量
	 */
	@Deprecated
	int AUTOWIRE_AUTODETECT = 4;

	/**
	 * Suffix for the "original instance" convention when initializing an existing bean instance: to be appended to the fully-qualified bean class name,
	 * e.g. "com.mypackage.MyClass.ORIGINAL", in order to enforce the given instance to be returned, i.e. no proxies etc.
	 * @since 5.1
	 * @see #initializeBean(Object, String)
	 * @see #applyBeanPostProcessorsBeforeInitialization(Object, String)
	 * @see #applyBeanPostProcessorsAfterInitialization(Object, String)
	 */
	String ORIGINAL_INSTANCE_SUFFIX = ".ORIGINAL";

	//-------------------------------------------------------------------------
	// Typical methods for creating and populating external bean instances
	//	创建和填充外部bean实例的典型方法
	//-------------------------------------------------------------------------
	/**
	 * Fully create a new bean instance of the given class.
	 * Performs full initialization of the bean, including all applicable {@link BeanPostProcessor BeanPostProcessors}.
	 * Note: This is intended for creating a fresh instance, populating annotated fields and methods as well as applying all standard bean initialization callbacks.
	 * It does <i>not</i> imply traditional by-name or by-type autowiring of properties; use {@link #createBean(Class, int, boolean)} for those purposes.
	 * @param beanClass the class of the bean to create
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 * 创建一个给定Class的实例。
	 * 执行此Bean所有的关于Bean生命周期的接口方法如BeanPostProcessor
	 * 此方法用于创建一个新实例，它会处理各种带有注解的域和方法，并且会调用所有Bean初始化时所需要调用的回调函数
	 * 此方法并不意味着by-name或者by-type方式的自动装配，如果需要使用这写功能，可以使用其重载方法
	 * 注意这个CreateBean和下面的CrateBean的不同，也就是说它只管给你创建Bean，但是不管给你根据Name或者Type进行注入哦
	 * 	当然，你可以显示在对应属性上指定@Autowired注解，让他也可以达到相同的效果
	 */
	<T> T createBean(Class<T> beanClass) throws BeansException;

	/**
	 * Populate the given bean instance through applying after-instantiation callbacks and bean property post-processing (e.g. for annotation-driven injection).
	 * Note: This is essentially intended for (re-)populating annotated fields and methods, either for new instances or for deserialized instances.
	 * It does <i>not</i> imply traditional by-name or by-type autowiring of properties;  use {@link #autowireBeanProperties} for those purposes.
	 * @param existingBean the existing bean instance
	 * @throws BeansException if wiring failed
	 * 通过调用给定Bean的after-instantiation及post-processing接口，对bean进行配置和填充。
	 * 此方法主要是用于处理Bean中带有注解的域和方法。
	 * 此方法并不意味着by-name或者by-type方式的自动装配，如果需要使用这写功能，可以使用其重载方法autowireBeanProperties
	 */
	void autowireBean(Object existingBean) throws BeansException;

	/**
	 * Configure the given raw bean: autowiring bean properties, applying bean property values,
	 * applying factory callbacks such as {@code setBeanName} and {@code setBeanFactory}, and also applying all bean post processors (including ones which might wrap the given raw bean).
	 * This is effectively a superset of what {@link #initializeBean} provides,fully applying the configuration specified by the corresponding bean definition.
	 * <b>Note: This method requires a bean definition for the given name!</b>
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean, to be passed to it if necessary (a bean definition of that name has to be available)
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException if there is no bean definition with the given name
	 * @throws BeansException if the initialization failed
	 * @see #initializeBean
	 * 配置参数中指定的bean，包括自动装配其域，对其应用如setBeanName功能的回调函数。
	 * 并且会调用其所有注册的post processor.
	 * 此方法提供的功能是initializeBean方法的超集，会应用所有注册在bean definenition中的操作。
	 * 不过需要BeanFactory 中有参数中指定名字的BeanDefinition。
	 */
	Object configureBean(Object existingBean, String beanName) throws BeansException;

	//-------------------------------------------------------------------------
	// Specialized methods for fine-grained control over the bean lifecycle
	// 用于细粒度控制bean生命周期的方法
	//-------------------------------------------------------------------------
	/**
	 * Fully create a new bean instance of the given class with the specified autowire strategy.
	 * All constants defined in this interface are supported here.
	 * Performs full initialization of the bean, including all applicable {@link BeanPostProcessor BeanPostProcessors}.
	 * This is effectively a superset of what {@link #autowire} provides, adding {@link #initializeBean} behavior.
	 * @param beanClass the class of the bean to create
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for objects (not applicable to autowiring a constructor, thus ignored there)
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 *  创建一个指定class的实例，通过参数可以指定其自动装配模式（by-name or by-type）.会执行所有注册在此class上用以初始化bean的方法，如BeanPostProcessors等
	 */
	Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * Instantiate a new bean instance of the given class with the specified autowire strategy. All constants defined in this interface are supported here.
	 * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply before-instantiation callbacks (e.g. for annotation-driven injection).
	 * Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors} callbacks or perform any further initialization of the bean.
	 * This interface  offers distinct, fine-grained operations for those purposes, for example {@link #initializeBean}.
	 * However, {@link InstantiationAwareBeanPostProcessor} callbacks are applied, if applicable to the construction of the instance.
	 * @param beanClass the class of the bean to instantiate
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for object references in the bean instance (not applicable to autowiring a constructor,thus ignored there)
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_AUTODETECT
	 * @see #initializeBean
	 * @see #applyBeanPostProcessorsBeforeInitialization
	 * @see #applyBeanPostProcessorsAfterInitialization
	 *  通过指定的自动装配策略来初始化一个Bean。此方法不会调用Bean上注册的诸如BeanPostProcessors的回调方法
	 */
	Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * Autowire the bean properties of the given bean instance by name or type.
	 * Can also be invoked with {@code AUTOWIRE_NO} in order to just apply after-instantiation callbacks (e.g. for annotation-driven injection).
	 * Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}  callbacks or perform any further initialization of the bean.
	 * This interface offers distinct, fine-grained operations for those purposes, for example {@link #initializeBean}.
	 * However, {@link InstantiationAwareBeanPostProcessor} callbacks are applied, if applicable to the configuration of the instance.
	 * @param existingBean the existing bean instance
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for object references in the bean instance
	 * @throws BeansException if wiring failed
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_NO
	 * 通过指定的自动装配方式来对给定的Bean进行自动装配。
	 * 不过会调用指定Bean注册的BeanPostProcessors等回调函数来初始化Bean。
	 * 如果指定装配方式为AUTOWIRE_NO的话，不会自动装配属性，但是依然会调用BeanPiostProcesser等回调方法。
	 */
	void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * Apply the property values of the bean definition with the given name to the given bean instance.
	 * The bean definition can either define a fully self-contained bean, reusing its property values, or just property values meant to be used for existing bean instances.
	 * This method does <i>not</i> autowire bean properties; it just applies explicitly defined property values.
	 * Use the {@link #autowireBeanProperties}  method to autowire an existing bean instance.
	 * <b>Note: This method requires a bean definition for the given name!</b>
	 * Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors} callbacks or perform any further initialization of the bean. This interface
	 * offers distinct, fine-grained operations for those purposes, for example {@link #initializeBean}.
	 * However, {@link InstantiationAwareBeanPostProcessor} callbacks are applied, if applicable to the configuration of the instance.
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean definition in the bean factory (a bean definition of that name has to be available)
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException if there is no bean definition with the given name
	 * @throws BeansException if applying the property values failed
	 * @see #autowireBeanProperties
	 * 自动装配存在的对象的属性
	 * 将参数中指定了那么的Bean，注入给定实例当中
	 * 此方法不会自动注入Bean的属性，它仅仅会应用在显式定义的属性之上。如果需要自动注入Bean属性，使用autowireBeanProperties方法。
	 * 此方法需要BeanFactory中存在指定名字的Bean。
	 * 除了InstantiationAwareBeanPostProcessor的回调方法外，此方法不会在Bean上应用其它的例如BeanPostProcessors等回调方法。
	 * 不过可以调用其他诸如initializeBean等方法来达到目的。
	 */
	void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

	/**
	 * 初始化参数中指定的Bean，调用任何其注册的回调函数如setBeanName、setBeanFactory等。
	 * 另外还会调用此Bean上的所有postProcessors 方法
	 * Initialize the given raw bean, applying factory callbacks such as {@code setBeanName} and {@code setBeanFactory},
	 * also applying all bean post processors (including ones which might wrap the given raw bean).
	 * Note that no bean definition of the given name has to exist in the bean factory.
	 * The passed-in bean name will simply be used for callbacks but not checked against the registered bean definitions.
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean, to be passed to it if necessary (only passed to {@link BeanPostProcessor BeanPostProcessors};
	 * can follow the {@link #ORIGINAL_INSTANCE_SUFFIX} convention in order to  enforce the given instance to be returned, i.e. no proxies etc)
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if the initialization failed
	 * @see #ORIGINAL_INSTANCE_SUFFIX
	 */
	Object initializeBean(Object existingBean, String beanName) throws BeansException;

	/**
	 * 调用参数中指定Bean的 postProcessBeforeInitialization 方法
	 * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean instance, invoking their {@code postProcessBeforeInitialization} methods.
	 * The returned bean instance may be a wrapper around the original.
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean, to be passed to it if necessary (only passed to {@link BeanPostProcessor BeanPostProcessors};
	 * can follow the {@link #ORIGINAL_INSTANCE_SUFFIX} convention in order to enforce the given instance to be returned, i.e. no proxies etc)
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if any post-processing failed
	 * @see BeanPostProcessor#postProcessBeforeInitialization
	 * @see #ORIGINAL_INSTANCE_SUFFIX
	 * 初始化前执行的方法
	 */
	Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException;

	/**
	 * 调用参数中指定Bean的 postProcessAfterInitialization 方法
	 * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean instance, invoking their {@code postProcessAfterInitialization} methods.
	 * The returned bean instance may be a wrapper around the original.
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean, to be passed to it if necessary (only passed to {@link BeanPostProcessor BeanPostProcessors};
	 * can follow the {@link #ORIGINAL_INSTANCE_SUFFIX} convention in order to enforce the given instance to be returned, i.e. no proxies etc)
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if any post-processing failed
	 * @see BeanPostProcessor#postProcessAfterInitialization
	 * @see #ORIGINAL_INSTANCE_SUFFIX
	 * 初始化后执行的方法
	 */
	Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException;

	/**
	 * 销毁参数中指定的Bean，同时调用此Bean上的DisposableBean和DestructionAwareBeanPostProcessors方法
	 * 在销毁途中，任何的异常情况都只应该被直接捕获和记录，而不应该向外抛出。
	 * Destroy the given bean instance (typically coming from {@link #createBean}),
	 * applying the {@link org.springframework.beans.factory.DisposableBean} contract as well as registered {@link DestructionAwareBeanPostProcessor DestructionAwareBeanPostProcessors}.
	 * Any exception that arises during destruction should be caught and logged instead of propagated to the caller of this method.
	 * @param existingBean the bean instance to destroy
	 */
	void destroyBean(Object existingBean);

	//-------------------------------------------------------------------------
	// Delegate methods for resolving injection points
	// 委托方法解决注入点
	//-------------------------------------------------------------------------
	/**
	 * 查找唯一符合指定类的实例，如果有，则返回实例的名字和实例本身 和BeanFactory中的getBean(Class)方法类似，只不过多加了一个bean的名字
	 * Resolve the bean instance that uniquely matches the given object type, if any,including its bean name.
	 * This is effectively a variant of {@link #getBean(Class)} which preserves the bean name of the matching instance.
	 * @param requiredType type the bean must match; can be an interface or superclass
	 * @return the bean name plus bean instance
	 * @throws NoSuchBeanDefinitionException if no matching bean was found
	 * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
	 * @throws BeansException if the bean could not be created
	 * @since 4.3.3
	 * @see #getBean(Class)
	 */
	<T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException;

	/**
	 * Resolve a bean instance for the given bean name, providing a dependency descriptor for exposure to target factory methods.
	 * This is effectively a variant of {@link #getBean(String, Class)} which supports factory methods with an {@link org.springframework.beans.factory.InjectionPoint} argument.
	 * @param name the name of the bean to look up
	 * @param descriptor the dependency descriptor for the requesting injection point
	 * @return the corresponding bean instance
	 * @throws NoSuchBeanDefinitionException if there is no bean with the specified name
	 * @throws BeansException if the bean could not be created
	 * @since 5.1.5
	 * @see #getBean(String, Class)
	 */
	Object resolveBeanByName(String name, DependencyDescriptor descriptor) throws BeansException;

	/**
	 * Resolve the specified dependency against the beans defined in this factory.
	 * @param descriptor the descriptor for the dependency (field/method/constructor)
	 * @param requestingBeanName the name of the bean which declares the given dependency
	 * @return the resolved object, or {@code null} if none found
	 * @throws NoSuchBeanDefinitionException if no matching bean was found
	 * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
	 * @throws BeansException if dependency resolution failed for any other reason
	 * @since 2.5
	 * @see #resolveDependency(DependencyDescriptor, String, Set, TypeConverter)
	 */
	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException;

	/**
	 * Resolve the specified dependency against the beans defined in this factory. 解析指定Bean在Factory中的依赖关系
	 * @param descriptor the descriptor for the dependency (field/method/constructor) 依赖描述
	 * @param requestingBeanName the name of the bean which declares the given dependency 依赖描述所属的Bean
	 * @param autowiredBeanNames a Set that all names of autowired beans (used for resolving the given dependency) are supposed to be added to 与指定Bean有依赖关系的Bean
	 * @param typeConverter the TypeConverter to use for populating arrays and collections 用以转换数组和链表的转换器
	 * @return the resolved object, or {@code null} if none found  解析结果，可能为null
	 * @throws NoSuchBeanDefinitionException if no matching bean was found
	 * @throws NoUniqueBeanDefinitionException if more than one matching bean was found
	 * @throws BeansException if dependency resolution failed for any other reason
	 * @since 2.5
	 * @see DependencyDescriptor
	 */
	@Nullable
	Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException;
}
