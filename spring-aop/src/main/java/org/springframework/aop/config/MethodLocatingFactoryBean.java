

package org.springframework.aop.config;

import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * {@link FactoryBean} implementation that locates a {@link Method} on a specified bean.
 * @since 2.0
 */
public class MethodLocatingFactoryBean implements FactoryBean<Method>, BeanFactoryAware {

	@Nullable
	private String targetBeanName;

	@Nullable
	private String methodName;

	@Nullable
	private Method method;

	/**
	 * Set the name of the bean to locate the {@link Method} on.
	 * This property is required.
	 * @param targetBeanName the name of the bean to locate the {@link Method} on
	 */
	public void setTargetBeanName(String targetBeanName) {
		this.targetBeanName = targetBeanName;
	}

	/**
	 * Set the name of the {@link Method} to locate.
	 * This property is required.
	 * @param methodName the name of the {@link Method} to locate
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (!StringUtils.hasText(this.targetBeanName)) {
			throw new IllegalArgumentException("Property 'targetBeanName' is required");
		}
		if (!StringUtils.hasText(this.methodName)) {
			throw new IllegalArgumentException("Property 'methodName' is required");
		}

		Class<?> beanClass = beanFactory.getType(this.targetBeanName);
		if (beanClass == null) {
			throw new IllegalArgumentException("Can't determine type of bean with name '" + this.targetBeanName + "'");
		}
		this.method = BeanUtils.resolveSignature(this.methodName, beanClass);
		if (this.method == null) {
			throw new IllegalArgumentException("Unable to locate method [" + this.methodName + "] on bean [" + this.targetBeanName + "]");
		}
	}


	@Override
	@Nullable
	public Method getObject() throws Exception {
		return this.method;
	}

	@Override
	public Class<Method> getObjectType() {
		return Method.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
