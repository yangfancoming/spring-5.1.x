

package org.springframework.beans.factory.config;

import java.util.Iterator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.lang.Nullable;

/**
 * Configuration interface to be implemented by most listable bean factories.
 * In addition to {@link ConfigurableBeanFactory}, it provides facilities to analyze and modify bean definitions, and to pre-instantiate singletons.
 *
 * This subinterface of {@link org.springframework.beans.factory.BeanFactory} is not meant to be used in normal application code: Stick to
 * {@link org.springframework.beans.factory.BeanFactory} or {@link org.springframework.beans.factory.ListableBeanFactory} for typical use cases.
 * This interface is just meant to allow for framework-internal plug'n'play even when needing access to bean factory configuration methods.
 * @since 03.11.2003
 * @see org.springframework.context.support.AbstractApplicationContext#getBeanFactory()
 *
 * 扩展了ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory接口,并提供了忽略依赖,自动装配判断,冻结bean的定义,枚举所有bean名称的功能
 * BeanFactory 的配置清单，指定忽略类型及接口。
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory, AutowireCapableBeanFactory, ConfigurableBeanFactory {

	/**
	 * 忽略自动装配的依赖类型
	 * Ignore the given dependency type for autowiring: for example, String. Default is none.
	 * @param type the dependency type to ignore
	 */
	void ignoreDependencyType(Class<?> type);

	/**
	 * Ignore the given dependency interface for autowiring.
	 * This will typically be used by application contexts to register dependencies that are resolved in other ways,
	 * like BeanFactory through BeanFactoryAware or ApplicationContext through ApplicationContextAware.
	 * By default, only the BeanFactoryAware interface is ignored.
	 * For further types to ignore, invoke this method for each type.
	 * @param ifc the dependency interface to ignore
	 * @see org.springframework.beans.factory.BeanFactoryAware
	 * @see org.springframework.context.ApplicationContextAware
	 *  忽略自动装配的接口
	 */
	void ignoreDependencyInterface(Class<?> ifc);

	/**
	 * Register a special dependency type with corresponding autowired value.
	 * This is intended for factory/context references that are supposed to be autowirable but are not defined as beans in the factory:
	 * e.g. a dependency of type ApplicationContext resolved to the ApplicationContext instance that the bean is living in.
	 * Note: There are no such default types registered in a plain BeanFactory,  not even for the BeanFactory interface itself.
	 * @param dependencyType the dependency type to register.
	 * This will typically  be a base interface such as BeanFactory,
	 * with extensions of it resolved  as well if declared as an autowiring dependency (e.g. ListableBeanFactory),
	 * as long as the given value actually implements the extended interface.
	 * @param autowiredValue the corresponding autowired value. This may also be an
	 * implementation of the {@link org.springframework.beans.factory.ObjectFactory} interface, which allows for lazy resolution of the actual target value.
	 *  使用相应的自动装配值注册特殊依赖关系类型
	 */
	void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue);

	/**
	 * Determine whether the specified bean qualifies as an autowire candidate,to be injected into other beans which declare a dependency of matching type.
	 * This method checks ancestor factories as well.
	 * @param beanName the name of the bean to check
	 * @param descriptor the descriptor of the dependency to resolve
	 * @return whether the bean should be considered as autowire candidate
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * 确定指定的bean是否有资格作为autowire候选者，注入到声明匹配类型依赖关系的其他bean中。
	 */
	boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException;

	/**
	 * Return the registered BeanDefinition for the specified bean, allowing access
	 * to its property values and constructor argument value (which can be modified during bean factory post-processing).
	 * A returned BeanDefinition object should not be a copy but the original definition object as registered in the factory.
	 * This means that it should be castable to a more specific implementation type, if necessary.
	 * <b>NOTE:</b> This method does <i>not</i> consider ancestor factories.
	 * It is only meant for accessing local bean definitions of this factory.
	 * @param beanName the name of the bean
	 * @return the registered BeanDefinition
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name defined in this factory
	 *  返回注册的Bean定义  返回指定bean的已注册BeanDefinition，允许访问其属性值和构造函数参数值（可以在bean工厂后处理期间修改）。
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Return a unified view over all bean names managed by this factory.
	 * Includes bean definition names as well as names of manually registered singleton instances, with bean definition names consistently coming first,
	 * analogous to how type/annotation specific retrieval of bean names works.
	 * @return the composite iterator for the bean names view
	 * @since 4.1.2
	 * @see #containsBeanDefinition
	 * @see #registerSingleton
	 * @see #getBeanNamesForType
	 * @see #getBeanNamesForAnnotation
	 * 返回所有bean名称的迭代对象
	 */
	Iterator<String> getBeanNamesIterator();

	/**
	 * Clear the merged bean definition cache, removing entries for beans which are not considered eligible for full metadata caching yet.
	 * Typically triggered after changes to the original bean definitions,
	 * e.g. after applying a {@link BeanFactoryPostProcessor}. Note that metadata for beans which have already been created at this point will be kept around.
	 * @since 4.2
	 * @see #getBeanDefinition
	 * @see #getMergedBeanDefinition
	 * 清除合并的bean定义缓存，删除尚未被认为有资格进行完整元数据缓存的bean条目
	 */
	void clearMetadataCache();

	/**
	 * Freeze all bean definitions, signalling that the registered bean definitions will not be modified or post-processed any further.
	 * This allows the factory to aggressively cache bean definition metadata.
	 * 冻结所有bean定义，表明注册的bean定义不会被修改或进一步后处理
	 */
	void freezeConfiguration();

	/**
	 * Return whether this factory's bean definitions are frozen,i.e. are not supposed to be modified or post-processed any further.
	 * @return {@code true} if the factory's configuration is considered frozen
	 * 返回是否冻结此工厂的bean定义
	 */
	boolean isConfigurationFrozen();

	/**
	 * 确保所有非lazy-init单例都已被实例化，同时考虑到 factorybeans
	 * Ensure that all non-lazy-init singletons are instantiated, also considering FactoryBeans {@link org.springframework.beans.factory.FactoryBean FactoryBeans}.
	 * Typically invoked at the end of factory setup, if desired. 如果需要，通常在工厂设置结束时调用
	 * @throws BeansException if one of the singleton beans could not be created.
	 * Note: This may have left the factory with some beans already initialized!
	 * Call {@link #destroySingletons()} for full cleanup in this case.
	 * @see #destroySingletons()
	 */
	void preInstantiateSingletons() throws BeansException;
}
