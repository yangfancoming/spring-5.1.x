

package org.springframework.beans.factory;

/**
 * Callback that allows a bean to be aware of the bean  {@link ClassLoader class loader};
 * that is, the class loader used by the present bean factory to load bean classes.
 *
 * This is mainly intended to be implemented by framework classes which
 * have to pick up application classes by name despite themselves potentially being loaded from a shared class loader.
 * For a list of all bean lifecycle methods, see the {@link BeanFactory BeanFactory javadocs}.
 * @since 2.0
 * @see BeanNameAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanClassLoaderAware extends Aware {

	/**
	 * Callback that supplies the bean {@link ClassLoader class loader} to  a bean instance.
	 * Invoked <i>after</i> the population of normal bean properties but <i>before</i> an initialization callback such as
	 * {@link InitializingBean InitializingBean's}
	 * {@link InitializingBean#afterPropertiesSet()}  method or a custom init-method.
	 * @param classLoader the owning class loader
	 */
	void setBeanClassLoader(ClassLoader classLoader);

}
