

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;

/**
 * Strategy interface for generating bean names for bean definitions.
 * @since 2.0.3
 * 我们知道在spring中每个bean都要有一个id或者name标示每个唯一的bean，
 * 在xml中定义一个bean可以指定其id和name值，但那些没有指定的，或者注解的spring的beanname怎么来的呢？
 * 这就是BeanNameGenerator接口实现的特性。
 *
 * BeanNameGenerator有两个实现版本(即2种名称生成策略)，DefaultBeanNameGenerator和AnnotationBeanNameGenerator。
 * 其中DefaultBeanNameGenerator是给xml配置文件加载bean时使用（BeanDefinitionReader中使用）；
 * AnnotationBeanNameGenerator 是为了处理注解生成bean name的情况。
 */
public interface BeanNameGenerator {

	/**
	 * Generate a bean name for the given bean definition.
	 * @param definition the bean definition to generate a name for  被生成名字的BeanDefinition实例；
	 * @param registry the bean definition registry that the given definition is supposed to be registered with，生成名字后注册进的BeanDefinitionRegistry。
	 * @return the generated bean name
	 */
	String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry);
}
