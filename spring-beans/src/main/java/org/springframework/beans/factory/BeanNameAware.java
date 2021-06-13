

package org.springframework.beans.factory;

/**
 * Interface to be implemented by beans that want to be aware of their bean name in a bean factory.
 * Note that it is not usually recommended that an object depends on its bean name,
 * as this represents a potentially brittle dependence on external configuration, as well as a possibly unnecessary dependence on a Spring API.
 * For a list of all bean lifecycle methods, see the {@link BeanFactory BeanFactory javadocs}.
 *
 * 注意，通常不建议对象依赖于它的bean名称，
 * 因为这表示对外部配置的潜在脆弱依赖，以及对springapi的可能不必要的依赖。
 * @since 01.11.2003
 * @see BeanClassLoaderAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanNameAware extends Aware {

	/**
	 * Set the name of the bean in the bean factory that created this bean.
	 * Invoked after population of normal bean properties but before an init callback such as {@link InitializingBean#afterPropertiesSet()} or a custom init-method.
	 * @param name the name of the bean in the factory.
	 * Note that this name is the actual bean name used in the factory, which may differ from the originally specified name:
	 * in particular for inner bean  names, the actual bean name might have been made unique through appending "#..." suffixes.
	 * Use the {@link BeanFactoryUtils#originalBeanName(String)} method to extract the original bean name (without suffix), if desired.
	 */
	void setBeanName(String name);

}
