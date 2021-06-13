

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * 允许自定义修改应用程序上下文的bean定义，修改上下文底层bean工厂的bean属性值。
 * 对于覆盖在应用程序上下文中配置的bean属性的针对系统管理员的自定义配置文件非常有用。
 * 请参阅{@link PropertyResourceConfigurer}及其具体实现，以了解解决这种配置需求的开箱即用解决方案。
 * 一个{@code BeanFactoryPostProcessor}可以与bean定义交互并修改bean定义，但从不与bean实例交互。
 * 这样做可能会导致过早的bean实例化，破坏容器并导致意外的副作用。
 * 这里说可以与bean的定义进行交互，但是不要和bean的实例进行交互，如果要和bean的实例进行交互，建议使用BeanPostProcessor扩展点
 *
 * 排序：
 * 1、自定检测到的bean会按照ordered和PriorityOrdered进行排序，order数字越大，优先级越低。
 * 2、对于编程的方式注册的后置处理器，他会按照注册的顺序进行排序,
 * 如果实现了order接口，也会被忽略：底层是一个list，编程的方式就是add

 * Allows for custom modification of an application context's bean definitions,adapting the bean property values of the context's underlying bean factory.
 * Application contexts can auto-detect BeanFactoryPostProcessor beans in their bean definitions and apply them before any other beans get created.
 * Useful for custom config files targeted at system administrators that override bean properties configured in the application context.
 * See PropertyResourceConfigurer and its concrete implementations for out-of-the-box solutions that address such configuration needs.
 *
 * A BeanFactoryPostProcessor may interact with and modify bean definitions, but never bean instances.
 * Doing so may cause premature bean instantiation, violating the container and causing unintended side-effects.
 * If bean instance interaction is required, consider implementing {@link BeanPostProcessor} instead.
 * @since 06.07.2003
 * @see BeanPostProcessor
 * @see PropertyResourceConfigurer
 * 用来 动态修改bean工厂中的属性  修改图纸 修改内存态
 * 说通俗一些就是可以管理我们的bean工厂内所有的beandefinition（未实例化）数据，可以随心所欲的修改属性。
 * BeanFactoryPostProcessor是实现spring容器功能扩展的重要接口，例如修改bean属性值，实现bean动态代理等。
 * 很多框架都是通过此接口实现对spring容器的扩展，例如mybatis与spring集成时，只定义了mapper接口，无实现类，但spring却可以完成自动注入，是不是很神奇？
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

	/**
	 * 在标准初始化之后修改应用程序上下文的内部bean工厂。
	 * 所有bean定义都已加载，但还没有实例化bean。这允许覆盖或添加属性，甚至可以在快速初始化bean中。
	 * 此后置处理器所在的阶段为bean定义获取之后，bean实例化之前，这里可以对其bean工厂进行改造，
	 * 方法：{@link ConfigurableListableBeanFactory#getBeanDefinition(java.lang.String)}
	 *
	 * Modify the application context's internal bean factory after its standard initialization.
	 * 在应用程序上下文的标准初始化之后修改其内部bean工厂
	 * All bean definitions will have been loaded, but no beans  will have been instantiated yet.
	 * 所有bean定义都将被加载，但还没有实例化bean。 (内存态)
	 * This allows for overriding or adding properties even to eager-initializing beans.
	 * 这样就可以覆盖或添加属性，甚至可以对渴望初始化的bean进行覆盖或添加
	 * @param beanFactory the bean factory used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
