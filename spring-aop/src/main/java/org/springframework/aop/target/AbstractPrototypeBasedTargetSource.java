

package org.springframework.aop.target;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

/**
 * Base class for dynamic {@link org.springframework.aop.TargetSource} implementations
 * that create new prototype bean instances to support a pooling or
 * new-instance-per-invocation strategy.
 *
 * Such TargetSources must run in a {@link BeanFactory}, as it needs to
 * call the {@code getBean} method to create a new prototype instance.
 * Therefore, this base class extends {@link AbstractBeanFactoryBasedTargetSource}.
 * @see org.springframework.beans.factory.BeanFactory#getBean
 * @see PrototypeTargetSource
 * @see ThreadLocalTargetSource
 * @see CommonsPool2TargetSource
 */
@SuppressWarnings("serial")
public abstract class AbstractPrototypeBasedTargetSource extends AbstractBeanFactoryBasedTargetSource {

	// 继承自BeanFactoryAware接口，将当前Spring使用的BeanFactory传进来
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		super.setBeanFactory(beanFactory);

		// Check whether the target bean is defined as prototype.
		if (!beanFactory.isPrototype(getTargetBeanName())) {
			throw new BeanDefinitionStoreException(
					"Cannot use prototype-based TargetSource against non-prototype bean with name '" + getTargetBeanName() + "': instances would not be independent");
		}
	}

	/**
	 * Subclasses should call this method to create a new prototype instance.
	 * @throws BeansException if bean creation failed
	 *  使用BeanFactory获取目标bean的对象，getTargetBeanName()方法将返回目标bean的名称，
	 *  由于目标bean是prototype类型的，因而这里也就可以通过BeanFactory获取prototype类型的bean
	 *  这也是PrototypeTargetSource能够生成prototype类型的bean的根本原因
	 */
	protected Object newPrototypeInstance() throws BeansException {
		if (logger.isDebugEnabled()) {
			logger.debug("Creating new instance of bean '" + getTargetBeanName() + "'");
		}
		return getBeanFactory().getBean(getTargetBeanName());
	}

	/**
	 * Subclasses should call this method to destroy an obsolete prototype instance.
	 * @param target the bean instance to destroy
	 *  如果生成的bean使用完成，则会调用当前方法销毁目标bean，由于目标bean可能实现了DisposableBean
	 *  接口，因而这里销毁bean的方式就是调用其实现的该接口的方法，从而销毁目标bean
	 */
	protected void destroyPrototypeInstance(Object target) {
		if (logger.isDebugEnabled()) {
			logger.debug("Destroying instance of bean '" + getTargetBeanName() + "'");
		}
		if (getBeanFactory() instanceof ConfigurableBeanFactory) {
			((ConfigurableBeanFactory) getBeanFactory()).destroyBean(getTargetBeanName(), target);
		}
		else if (target instanceof DisposableBean) {
			try {
				((DisposableBean) target).destroy();
			}
			catch (Throwable ex) {
				logger.warn("Destroy method on bean with name '" + getTargetBeanName() + "' threw an exception", ex);
			}
		}
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		throw new NotSerializableException("A prototype-based TargetSource itself is not deserializable - just a disconnected SingletonTargetSource or EmptyTargetSource is");

	}

	/**
	 * Replaces this object with a SingletonTargetSource on serialization.
	 * Protected as otherwise it won't be invoked for subclasses.
	 * (The {@code writeReplace()} method must be visible to the class being serialized.)
	 * With this implementation of this method, there is no need to mark
	 * non-serializable fields in this class or subclasses as transient.
	 */
	protected Object writeReplace() throws ObjectStreamException {
		if (logger.isDebugEnabled()) {
			logger.debug("Disconnecting TargetSource [" + this + "]");
		}
		try {
			// Create disconnected SingletonTargetSource/EmptyTargetSource.
			Object target = getTarget();
			return (target != null ? new SingletonTargetSource(target) : EmptyTargetSource.forClass(getTargetClass()));
		}catch (Exception ex) {
			String msg = "Cannot get target for disconnecting TargetSource [" + this + "]";
			logger.error(msg, ex);
			throw new NotSerializableException(msg + ": " + ex);
		}
	}

}
