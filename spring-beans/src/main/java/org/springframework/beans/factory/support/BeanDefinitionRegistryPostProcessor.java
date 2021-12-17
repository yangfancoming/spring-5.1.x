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
 * BeanDefinitionRegistryPostProcessor 在BeanFactoryPostProcessor执行之前。更侧重于修改bean定义的注册信息，
 * 而BeanFactoryPostProcessor 更侧重于修改Bean工厂的信息，虽然也可以直接注册bean对象。
 *
 * 官网的建议是BeanDefinitionRegistryPostProcessor用来添加额外的bd，而 BeanFactoryPostProcessor 用来修改bd。
 * 对标准{@link BeanFactoryPostProcessor}SPI的扩展，允许在常规BeanFactoryPostProcessor检测启动之前注册进一步的bean定义。
 * 特别地，BeanDefinitionRegistryPostProcessor可以注册更多的bean定义，这些定义反过来定义BeanFactoryPostProcessor实例。
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

	/**
	 * Modify the application context's internal bean definition registry after its standard initialization.
	 * All regular bean definitions will have been loaded,but no beans will have been instantiated yet.
	 * This allows for adding further bean definitions before the next post-processing phase kicks in.
	 * @param registry the bean definition registry used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 * 在标准初始化之后修改应用程序上下文的内部bean定义注册表。
	 * 这允许在下一个后期处理阶段开始之前添加更多bean定义。
	 */
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;
}
