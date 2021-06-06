

package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

/**
 * Extension to the standard {@link BeanFactoryPostProcessor} SPI,allowing for the registration of further bean definitions <i>before</i> regular BeanFactoryPostProcessor detection kicks in.
 * In particular,BeanDefinitionRegistryPostProcessor may register further bean definitions which in turn define BeanFactoryPostProcessor instances.
 * @since 3.0.1
 * @see org.springframework.context.annotation.ConfigurationClassPostProcessor
 * 用于在创建bean之前增加或改变BeanDefinition
 * BeanDefinitionRegistryPostProcessor 与 BeanFactoryPostProcessor 的区别： 看2个接口方法的参数便知：
 * BeanDefinitionRegistryPostProcessor 更侧重于在BeanFactoryPostProcessor执行之前，处理bean定义的注册信息，
 * BeanFactoryPostProcessor 更侧重于修改Bean工厂的信息，也可以直接注册bean对象。
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

	/**
	 * Modify the application context's internal bean definition registry after its standard initialization.
	 * All regular bean definitions will have been loaded,but no beans will have been instantiated yet.
	 * This allows for adding further bean definitions before the next post-processing phase kicks in.
	 * @param registry the bean definition registry used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;
}
