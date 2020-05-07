

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * Allows for custom modification of an application context's bean definitions,adapting the bean property values of the context's underlying bean factory.
 * Application contexts can auto-detect BeanFactoryPostProcessor beans in  their bean definitions and apply them before any other beans get created.
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
 * 说通俗一些就是可以管理我们的bean工厂内所有的beandefinition（未实例化）数据，可以随心所欲的修改属性
 *
 * BeanFactoryPostProcessor是实现spring容器功能扩展的重要接口，例如修改bean属性值，实现bean动态代理等。
 * 很多框架都是通过此接口实现对spring容器的扩展，例如mybatis与spring集成时，只定义了mapper接口，无实现类，但spring却可以完成自动注入，是不是很神奇？
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

	/**
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
