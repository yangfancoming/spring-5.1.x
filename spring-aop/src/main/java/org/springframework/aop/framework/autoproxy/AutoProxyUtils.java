

package org.springframework.aop.framework.autoproxy;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Utilities for auto-proxy aware components.
 * Mainly for internal use within the framework.
 * @since 2.0.3
 * @see AbstractAutoProxyCreator
 */
public abstract class AutoProxyUtils {

	/**
	 * Bean definition attribute that may indicate whether a given bean is supposed to be proxied with its target class (in case of it getting proxied in the first place).
	 * The value is {@code Boolean.TRUE} or {@code Boolean.FALSE}.
	 * Proxy factories can set this attribute if they built a target class proxy for a specific bean,
	 * and want to enforce that bean can always be cast  to its target class (even if AOP advices get applied through auto-proxying).
	 * @see #shouldProxyTargetClass
	 *
	 * Bean定义属性，该属性可以指示给定Bean是否应该用其目标类进行代理（如果它首先被代理）。
	 * 值为{@code Boolean.TRUE}或{@code Boolean.FALSE}。
	 * 如果代理工厂为特定bean构建了目标类代理，则可以设置此属性，
	 * 并希望强制bean始终可以被强制转换到其目标类（即使AOP建议通过自动代理得到应用）
	 * org.springframework.aop.framework.autoproxy.AutoProxyUtils.preserveTargetClass
	 */
	public static final String PRESERVE_TARGET_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(AutoProxyUtils.class, "preserveTargetClass");

	/**
	 * Bean definition attribute that indicates the original target class of an auto-proxied bean,
	 * e.g. to be used for the introspection of annotations on the target class behind an interface-based proxy.
	 * @since 4.2.3
	 * @see #determineTargetClass
	 */
	public static final String ORIGINAL_TARGET_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(AutoProxyUtils.class, "originalTargetClass");


	/**
	 * Determine whether the given bean should be proxied with its target class rather than its interfaces.
	 * Checks the {@link #PRESERVE_TARGET_CLASS_ATTRIBUTE "preserveTargetClass" attribute} of the corresponding bean definition.
	 * @param beanFactory the containing ConfigurableListableBeanFactory
	 * @param beanName the name of the bean
	 * @return whether the given bean should be proxied with its target class
	 */
	public static boolean shouldProxyTargetClass(ConfigurableListableBeanFactory beanFactory, @Nullable String beanName) {
		if (beanName != null && beanFactory.containsBeanDefinition(beanName)) {
			BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
			return Boolean.TRUE.equals(bd.getAttribute(PRESERVE_TARGET_CLASS_ATTRIBUTE));
		}
		return false;
	}

	/**
	 * Determine the original target class for the specified bean, if possible,otherwise falling back to a regular {@code getType} lookup.
	 * @param beanFactory the containing ConfigurableListableBeanFactory
	 * @param beanName the name of the bean
	 * @return the original target class as stored in the bean definition, if any
	 * @since 4.2.3
	 * @see org.springframework.beans.factory.BeanFactory#getType(String)
	 */
	@Nullable
	public static Class<?> determineTargetClass(ConfigurableListableBeanFactory beanFactory, @Nullable String beanName) {
		if (beanName == null) {
			return null;
		}
		if (beanFactory.containsBeanDefinition(beanName)) {
			BeanDefinition bd = beanFactory.getMergedBeanDefinition(beanName);
			Class<?> targetClass = (Class<?>) bd.getAttribute(ORIGINAL_TARGET_CLASS_ATTRIBUTE);
			if (targetClass != null) {
				return targetClass;
			}
		}
		return beanFactory.getType(beanName);
	}

	/**
	 * Expose the given target class for the specified bean, if possible.
	 * @param beanFactory the containing ConfigurableListableBeanFactory
	 * @param beanName the name of the bean
	 * @param targetClass the corresponding target class
	 * @since 4.2.3
	 */
	static void exposeTargetClass(ConfigurableListableBeanFactory beanFactory, @Nullable String beanName, Class<?> targetClass) {
		if (beanName != null && beanFactory.containsBeanDefinition(beanName)) {
			beanFactory.getMergedBeanDefinition(beanName).setAttribute(ORIGINAL_TARGET_CLASS_ATTRIBUTE, targetClass);
		}
	}

	/**
	 * Determine whether the given bean name indicates an "original instance"
	 * according to {@link AutowireCapableBeanFactory#ORIGINAL_INSTANCE_SUFFIX},skipping any proxy attempts for it.
	 * @param beanName the name of the bean
	 * @param beanClass the corresponding bean class
	 * @since 5.1
	 * @see AutowireCapableBeanFactory#ORIGINAL_INSTANCE_SUFFIX
	 */
	static boolean isOriginalInstance(String beanName, Class<?> beanClass) {
		if (!StringUtils.hasLength(beanName) || beanName.length() != beanClass.getName().length() + AutowireCapableBeanFactory.ORIGINAL_INSTANCE_SUFFIX.length()) {
			return false;
		}
		return (beanName.startsWith(beanClass.getName()) && beanName.endsWith(AutowireCapableBeanFactory.ORIGINAL_INSTANCE_SUFFIX));
	}
}
