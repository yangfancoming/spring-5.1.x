

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * Factory hook that allows for custom modification of new bean instances, e.g. checking for marker interfaces or wrapping them with proxies.
 * 允许自定义修改新的bean实例的工厂钩子，例如检查标记接口或用代理包装它们。
 *
 * ApplicationContexts can autodetect BeanPostProcessor beans in their bean definitions and apply them to any beans subsequently created.
 * 应用上下文可以在bean定义中自动检测 后置处理的bean，并将其应用于随后创建的任何bean。
 * Plain bean factories allow for programmatic registration of post-processors,applying to all beans created through this factory.
 * 纯bean工厂允许对后处理器进行编程注册，应用于通过该工厂创建的所有bean。
 *
 * Typically, post-processors that populate beans via marker interfaces or the like will implement {@link #postProcessBeforeInitialization},
 * while post-processors that wrap beans with proxies will normally implement {@link #postProcessAfterInitialization}.
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor
 * @see DestructionAwareBeanPostProcessor
 * @see ConfigurableBeanFactory#addBeanPostProcessor
 * @see BeanFactoryPostProcessor
 *
 * 用来 动态修改bean示例的属性  修改实例  修改成熟态
 */
public interface BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>before</i> any bean initialization callbacks (like InitializingBean's {@code afterPropertiesSet} or a custom init-method).
	 * The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 * The default implementation returns the given {@code bean} as-is.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	// postProcessBeforeInitialization 方法会在每一个bean对象的初始化方法调用之前回调；
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean
	 * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 * In case of a FactoryBean, this callback will be invoked for both the FactoryBean
	 * instance and the objects created by the FactoryBean (as of Spring 2.0). The
	 * post-processor can decide whether to apply to either the FactoryBean or created
	 * objects or both through corresponding {@code bean instanceof FactoryBean} checks.
	 * This callback will also be invoked after a short-circuiting triggered by a
	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation} method,
	 * in contrast to all other BeanPostProcessor callbacks.
	 * The default implementation returns the given {@code bean} as-is.
	 * @param bean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one;
	 * if {@code null}, no subsequent BeanPostProcessors will be invoked
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
