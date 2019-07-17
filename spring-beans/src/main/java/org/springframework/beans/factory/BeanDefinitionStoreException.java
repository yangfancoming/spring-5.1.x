

package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;
import org.springframework.lang.Nullable;

/**
 * Exception thrown when a BeanFactory encounters an invalid bean definition:
 * e.g. in case of incomplete or contradictory bean metadata.
 *
 * @author Rod Johnson

 * @author Rob Harrop
 */
@SuppressWarnings("serial")
public class BeanDefinitionStoreException extends FatalBeanException {

	@Nullable
	private final String resourceDescription;

	@Nullable
	private final String beanName;


	/**
	 * Create a new BeanDefinitionStoreException.
	 * @param msg the detail message (used as exception message as-is)
	 */
	public BeanDefinitionStoreException(String msg) {
		super(msg);
		this.resourceDescription = null;
		this.beanName = null;
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * @param msg the detail message (used as exception message as-is)
	 * @param cause the root cause (may be {@code null})
	 */
	public BeanDefinitionStoreException(String msg, @Nullable Throwable cause) {
		super(msg, cause);
		this.resourceDescription = null;
		this.beanName = null;
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * @param resourceDescription description of the resource that the bean definition came from
	 * @param msg the detail message (used as exception message as-is)
	 */
	public BeanDefinitionStoreException(@Nullable String resourceDescription, String msg) {
		super(msg);
		this.resourceDescription = resourceDescription;
		this.beanName = null;
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * @param resourceDescription description of the resource that the bean definition came from
	 * @param msg the detail message (used as exception message as-is)
	 * @param cause the root cause (may be {@code null})
	 */
	public BeanDefinitionStoreException(@Nullable String resourceDescription, String msg, @Nullable Throwable cause) {
		super(msg, cause);
		this.resourceDescription = resourceDescription;
		this.beanName = null;
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * @param resourceDescription description of the resource that the bean definition came from
	 * @param beanName the name of the bean
	 * @param msg the detail message (appended to an introductory message that indicates
	 * the resource and the name of the bean)
	 */
	public BeanDefinitionStoreException(@Nullable String resourceDescription, String beanName, String msg) {
		this(resourceDescription, beanName, msg, null);
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * @param resourceDescription description of the resource that the bean definition came from
	 * @param beanName the name of the bean
	 * @param msg the detail message (appended to an introductory message that indicates
	 * the resource and the name of the bean)
	 * @param cause the root cause (may be {@code null})
	 */
	public BeanDefinitionStoreException(
			@Nullable String resourceDescription, String beanName, String msg, @Nullable Throwable cause) {

		super("Invalid bean definition with name '" + beanName + "' defined in " + resourceDescription + ": " + msg,
				cause);
		this.resourceDescription = resourceDescription;
		this.beanName = beanName;
	}


	/**
	 * Return the description of the resource that the bean definition came from, if available.
	 */
	@Nullable
	public String getResourceDescription() {
		return this.resourceDescription;
	}

	/**
	 * Return the name of the bean, if available.
	 */
	@Nullable
	public String getBeanName() {
		return this.beanName;
	}

}
