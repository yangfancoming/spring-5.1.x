

package org.springframework.aop.aspectj;

import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;

/**
 * Interface implemented to provide an instance of an AspectJ aspect.
 * Decouples from Spring's bean factory.
 *
 * <p>Extends the {@link org.springframework.core.Ordered} interface
 * to express an order value for the underlying aspect in a chain.
 *
 * @author Rod Johnson

 * @since 2.0
 * @see org.springframework.beans.factory.BeanFactory#getBean
 */
public interface AspectInstanceFactory extends Ordered {

	/**
	 * Create an instance of this factory's aspect.
	 * @return the aspect instance (never {@code null})
	 */
	Object getAspectInstance();

	/**
	 * Expose the aspect class loader that this factory uses.
	 * @return the aspect class loader (or {@code null} for the bootstrap loader)
	 * @see org.springframework.util.ClassUtils#getDefaultClassLoader()
	 */
	@Nullable
	ClassLoader getAspectClassLoader();

}
