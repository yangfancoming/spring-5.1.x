

package org.springframework.beans.factory.config;

/**
 * Callback for customizing a given bean definition.
 * Designed for use with a lambda expression or method reference.
 *

 * @since 5.0
 * @see org.springframework.beans.factory.support.BeanDefinitionBuilder#applyCustomizers
 */
@FunctionalInterface
public interface BeanDefinitionCustomizer {

	/**
	 * Customize the given bean definition.
	 */
	void customize(BeanDefinition bd);

}
