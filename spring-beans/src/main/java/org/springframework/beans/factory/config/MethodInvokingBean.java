

package org.springframework.beans.factory.config;

import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.support.ArgumentConvertingMethodInvoker;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/**
 * Simple method invoker bean: just invoking a target method, not expecting a result
 * to expose to the container (in contrast to {@link MethodInvokingFactoryBean}).
 *
 * <p>This invoker supports any kind of target method. A static method may be specified
 * by setting the {@link #setTargetMethod targetMethod} property to a String representing
 * the static method name, with {@link #setTargetClass targetClass} specifying the Class
 * that the static method is defined on. Alternatively, a target instance method may be
 * specified, by setting the {@link #setTargetObject targetObject} property as the target
 * object, and the {@link #setTargetMethod targetMethod} property as the name of the
 * method to call on that target object. Arguments for the method invocation may be
 * specified by setting the {@link #setArguments arguments} property.
 *
 * <p>This class depends on {@link #afterPropertiesSet()} being called once
 * all properties have been set, as per the InitializingBean contract.
 *
 * <p>An example (in an XML based bean factory definition) of a bean definition
 * which uses this class to call a static initialization method:
 *
 * <pre class="code">
 * &lt;bean id="myObject" class="org.springframework.beans.factory.config.MethodInvokingBean">
 *   &lt;property name="staticMethod" value="com.whatever.MyClass.init"/>
 * &lt;/bean></pre>
 *
 * <p>An example of calling an instance method to start some server bean:
 *
 * <pre class="code">
 * &lt;bean id="myStarter" class="org.springframework.beans.factory.config.MethodInvokingBean">
 *   &lt;property name="targetObject" ref="myServer"/>
 *   &lt;property name="targetMethod" value="start"/>
 * &lt;/bean></pre>
 *

 * @since 4.0.3
 * @see MethodInvokingFactoryBean
 * @see org.springframework.util.MethodInvoker
 */
public class MethodInvokingBean extends ArgumentConvertingMethodInvoker
		implements BeanClassLoaderAware, BeanFactoryAware, InitializingBean {

	@Nullable
	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	@Nullable
	private ConfigurableBeanFactory beanFactory;


	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	@Override
	protected Class<?> resolveClassName(String className) throws ClassNotFoundException {
		return ClassUtils.forName(className, this.beanClassLoader);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		if (beanFactory instanceof ConfigurableBeanFactory) {
			this.beanFactory = (ConfigurableBeanFactory) beanFactory;
		}
	}

	/**
	 * Obtain the TypeConverter from the BeanFactory that this bean runs in,
	 * if possible.
	 * @see ConfigurableBeanFactory#getTypeConverter()
	 */
	@Override
	protected TypeConverter getDefaultTypeConverter() {
		if (this.beanFactory != null) {
			return this.beanFactory.getTypeConverter();
		}
		else {
			return super.getDefaultTypeConverter();
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		prepare();
		invokeWithTargetException();
	}

	/**
	 * Perform the invocation and convert InvocationTargetException
	 * into the underlying target exception.
	 */
	@Nullable
	protected Object invokeWithTargetException() throws Exception {
		try {
			return invoke();
		}
		catch (InvocationTargetException ex) {
			if (ex.getTargetException() instanceof Exception) {
				throw (Exception) ex.getTargetException();
			}
			if (ex.getTargetException() instanceof Error) {
				throw (Error) ex.getTargetException();
			}
			throw ex;
		}
	}

}
