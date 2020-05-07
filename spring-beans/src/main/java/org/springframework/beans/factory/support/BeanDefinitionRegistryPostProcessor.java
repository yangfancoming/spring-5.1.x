

package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

/**
 * Extension to the standard {@link BeanFactoryPostProcessor} SPI,allowing for the registration of further bean definitions <i>before</i> regular BeanFactoryPostProcessor detection kicks in.
 * In particular,BeanDefinitionRegistryPostProcessor may register further bean definitions which in turn define BeanFactoryPostProcessor instances.
 * @since 3.0.1
 * @see org.springframework.context.annotation.ConfigurationClassPostProcessor
 * 用于在创建bean之前增加或改变BeanDefinition
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
