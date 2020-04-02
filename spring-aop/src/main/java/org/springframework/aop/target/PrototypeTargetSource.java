

package org.springframework.aop.target;

import org.springframework.beans.BeansException;

/**
 * {@link org.springframework.aop.TargetSource} implementation that
 * creates a new instance of the target bean for each request,
 * destroying each instance on release (after each request).
 *
 * Obtains bean instances from its containing
 * {@link org.springframework.beans.factory.BeanFactory}.

 * @see #setBeanFactory
 * @see #setTargetBeanName
 */
@SuppressWarnings("serial")
public class PrototypeTargetSource extends AbstractPrototypeBasedTargetSource {

	/**
	 * Obtain a new prototype instance for every call.
	 * @see #newPrototypeInstance()
	 */
	@Override
	public Object getTarget() throws BeansException {
		return newPrototypeInstance();
	}

	/**
	 * Destroy the given independent instance.
	 * @see #destroyPrototypeInstance
	 */
	@Override
	public void releaseTarget(Object target) {
		destroyPrototypeInstance(target);
	}

	@Override
	public String toString() {
		return "PrototypeTargetSource for target bean with name '" + getTargetBeanName() + "'";
	}

}
