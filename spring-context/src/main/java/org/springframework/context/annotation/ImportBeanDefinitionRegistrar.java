

package org.springframework.context.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Interface to be implemented by types that register additional bean definitions when processing @{@link Configuration} classes.
 * Useful when operating at the bean definition level (as opposed to {@code @Bean} method/instance level) is desired or necessary.
 * Along with {@code @Configuration} and {@link ImportSelector}, classes of this type may be provided to the @{@link Import} annotation (or may also be returned from an {@code ImportSelector}).
 * An {@link ImportBeanDefinitionRegistrar} may implement any of the following  {@link org.springframework.beans.factory.Aware Aware} interfaces,
 * and their respective methods will be called prior to {@link #registerBeanDefinitions}:
 * <li>{@link org.springframework.context.EnvironmentAware EnvironmentAware}</li>
 * <li>{@link org.springframework.beans.factory.BeanFactoryAware BeanFactoryAware}
 * <li>{@link org.springframework.beans.factory.BeanClassLoaderAware BeanClassLoaderAware}
 * <li>{@link org.springframework.context.ResourceLoaderAware ResourceLoaderAware}
 * See implementations and associated unit tests for usage examples.
 * @since 3.1
 * @see Import
 * @see ImportSelector
 * @see Configuration
 * 1.此接口的实现类，只能通过其他类的@Import方式来加载，通常是启动类或配置类。
 * 2.在@Configuration注解的配置类上使用@Import导入实现类，如果括号中的类是ImportBeanDefinitionRegistrar的实现类，则会调用接口方法，将其中要注册的类注册成bean。
 * 3.实现该接口的类拥有注册bean的能力。
 */
public interface ImportBeanDefinitionRegistrar {

	/**
	 * Register bean definitions as necessary based on the given annotation metadata of the importing {@code @Configuration} class.
	 * 根据导入@configuration类 给定的注解元数据，按需注册bean定义。 eg: @EnableAspectJAutoProxy 注解
	 * Note that {@link BeanDefinitionRegistryPostProcessor} types may <em>not</em> be
	 * registered here, due to lifecycle constraints related to {@code @Configuration} class processing.
	 * @param importingClassMetadata annotation metadata of the importing class  当前类的注解信息，实现类为 StandardAnnotationMetadata
	 * @param registry current bean definition registry   BeanDefinition注册中心，实现类为 DefaultListableBeanFactory
	 */
	void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry);
}
