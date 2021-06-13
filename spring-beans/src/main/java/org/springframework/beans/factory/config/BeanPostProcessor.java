

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * Factory hook that allows for custom modification of new bean instances, e.g. checking for marker interfaces or wrapping them with proxies.
 * ApplicationContexts can autodetect BeanPostProcessor beans in their bean definitions and apply them to any beans subsequently created.
 * Plain bean factories allow for programmatic registration of post-processors,applying to all beans created through this factory.
 * Typically, post-processors that populate beans via marker interfaces or the like will implement {@link #postProcessBeforeInitialization},
 * while post-processors that wrap beans with proxies will normally implement {@link #postProcessAfterInitialization}.
 *
 * 允许自定义修改新的bean实例的工厂钩子，例如检查标记接口或用代理包装它们。
 * 应用上下文可以在bean定义中自动检测 后置处理的bean，并将其应用于随后创建的任何bean。
 * 纯bean工厂允许对后处理器进行编程注册，应用于通过该工厂创建的所有bean。
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor
 * @see DestructionAwareBeanPostProcessor
 * @see ConfigurableBeanFactory#addBeanPostProcessor
 * @see BeanFactoryPostProcessor
 * 用来 动态修改bean示例的属性  修改实例  修改成熟态
 * sos 应用场景：
 * 	1，可以解析bean中的一些注解转化为需要的属性
 *  2，注入处理一些统一的属性，而不用在每个bean中注入
 *  3，在Spring开发过程中，存在同一个接口有多个实现类的情况，根据不同的应用场景，通常在具体调用的地方来选择不同的接口实现类，Spring的BeanPostProcessor是个好的选择。
 */
public interface BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>before</i> any bean initialization callbacks (like InitializingBean's {@code afterPropertiesSet} or a custom init-method).
	 * The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 * The default implementation returns the given {@code bean} as-is.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one; if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	// postProcessBeforeInitialization 方法会在每一个bean对象的初始化方法调用之前回调；
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean initialization callbacks (like InitializingBean's {@code afterPropertiesSet} or a custom init-method).
	 * The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 * In case of a FactoryBean, this callback will be invoked for both the FactoryBean instance and the objects created by the FactoryBean (as of Spring 2.0).
	 * The post-processor can decide whether to apply to either the FactoryBean or created
	 * objects or both through corresponding {@code bean instanceof FactoryBean} checks.
	 * This callback will also be invoked after a short-circuiting triggered by a
	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation} method,in contrast to all other BeanPostProcessor callbacks.
	 * The default implementation returns the given {@code bean} as-is.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.FactoryBean
	 */
	// postProcessAfterInitialization 方法会在每个bean对象的初始化方法调用之后被回调
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}
}
