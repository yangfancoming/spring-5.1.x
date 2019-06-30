

package org.springframework.context.support;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

/**
 * {@link org.springframework.context.ApplicationContext} implementation
 * which supports programmatic registration of beans and messages,
 * rather than reading bean definitions from external configuration sources.
 * Mainly useful for testing.

 * @see #registerSingleton
 * @see #registerPrototype
 * @see #registerBeanDefinition
 * @see #refresh
 */
// 通过编程的方式去启动一个bean容器---该类通常用于测试，用于加载任意的外部资源，而不用加载特定格式的文件
public class StaticApplicationContext extends GenericApplicationContext {

	private final StaticMessageSource staticMessageSource;


	/**
	 * Create a new StaticApplicationContext.
	 * @see #registerSingleton
	 * @see #registerPrototype
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public StaticApplicationContext() throws BeansException {
		this(null);
	}

	/**
	 * Create a new StaticApplicationContext with the given parent.
	 * @see #registerSingleton
	 * @see #registerPrototype
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public StaticApplicationContext(@Nullable ApplicationContext parent) throws BeansException {
		super(parent);

		// Initialize and register a StaticMessageSource.
		this.staticMessageSource = new StaticMessageSource();
		getBeanFactory().registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.staticMessageSource);
	}


	/**
	 * Overridden to turn it into a no-op, to be more lenient towards test cases.
	 */
	@Override
	protected void assertBeanFactoryActive() {
	}

	/**
	 * Return the internal StaticMessageSource used by this context.
	 * Can be used to register messages on it.
	 * @see #addMessage
	 */
	public final StaticMessageSource getStaticMessageSource() {
		return this.staticMessageSource;
	}

	/**
	 * Register a singleton bean with the underlying bean factory.
	 * <p>For more advanced needs, register with the underlying BeanFactory directly.
	 * @see #getDefaultListableBeanFactory
	 */
	public void registerSingleton(String name, Class<?> clazz) throws BeansException {
		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setBeanClass(clazz);
		getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
	}

	/**
	 * Register a singleton bean with the underlying bean factory.
	 * <p>For more advanced needs, register with the underlying BeanFactory directly.
	 * @see #getDefaultListableBeanFactory
	 */
	public void registerSingleton(String name, Class<?> clazz, MutablePropertyValues pvs) throws BeansException {
		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setBeanClass(clazz);
		bd.setPropertyValues(pvs);
		getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
	}

	/**
	 * Register a prototype bean with the underlying bean factory.
	 * <p>For more advanced needs, register with the underlying BeanFactory directly.
	 * @see #getDefaultListableBeanFactory
	 */
	public void registerPrototype(String name, Class<?> clazz) throws BeansException {
		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setScope(GenericBeanDefinition.SCOPE_PROTOTYPE);
		bd.setBeanClass(clazz);
		getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
	}

	/**
	 * Register a prototype bean with the underlying bean factory.
	 * <p>For more advanced needs, register with the underlying BeanFactory directly.
	 * @see #getDefaultListableBeanFactory
	 */
	public void registerPrototype(String name, Class<?> clazz, MutablePropertyValues pvs) throws BeansException {
		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setScope(GenericBeanDefinition.SCOPE_PROTOTYPE);
		bd.setBeanClass(clazz);
		bd.setPropertyValues(pvs);
		getDefaultListableBeanFactory().registerBeanDefinition(name, bd);
	}

	/**
	 * Associate the given message with the given code.
	 * @param code lookup code
	 * @param locale locale message should be found within
	 * @param defaultMessage message associated with this lookup code
	 * @see #getStaticMessageSource
	 */
	public void addMessage(String code, Locale locale, String defaultMessage) {
		getStaticMessageSource().addMessage(code, locale, defaultMessage);
	}

}
