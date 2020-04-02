

package org.springframework.validation.beanvalidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

/**
 * JSR-303 {@link ConstraintValidatorFactory} implementation that delegates to a
 * Spring BeanFactory for creating autowired {@link ConstraintValidator} instances.
 *
 * Note that this class is meant for programmatic use, not for declarative use
 * in a standard {@code validation.xml} file. Consider
 * {@link org.springframework.web.bind.support.SpringWebConstraintValidatorFactory}
 * for declarative use in a web application, e.g. with JAX-RS or JAX-WS.
 *

 * @since 3.0
 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory#createBean(Class)
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public class SpringConstraintValidatorFactory implements ConstraintValidatorFactory {

	private final AutowireCapableBeanFactory beanFactory;


	/**
	 * Create a new SpringConstraintValidatorFactory for the given BeanFactory.
	 * @param beanFactory the target BeanFactory
	 */
	public SpringConstraintValidatorFactory(AutowireCapableBeanFactory beanFactory) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
	}


	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
		return this.beanFactory.createBean(key);
	}

	// Bean Validation 1.1 releaseInstance method
	public void releaseInstance(ConstraintValidator<?, ?> instance) {
		this.beanFactory.destroyBean(instance);
	}

}
