

package org.springframework.jca.context;

import javax.resource.spi.BootstrapContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor}
 * implementation that passes the BootstrapContext to beans that implement
 * the {@link BootstrapContextAware} interface.
 *
 * {@link ResourceAdapterApplicationContext} automatically registers
 * this processor with its underlying bean factory.
 *

 * @since 2.5
 * @see BootstrapContextAware
 */
class BootstrapContextAwareProcessor implements BeanPostProcessor {

	@Nullable
	private final BootstrapContext bootstrapContext;


	/**
	 * Create a new BootstrapContextAwareProcessor for the given context.
	 */
	public BootstrapContextAwareProcessor(@Nullable BootstrapContext bootstrapContext) {
		this.bootstrapContext = bootstrapContext;
	}


	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (this.bootstrapContext != null && bean instanceof BootstrapContextAware) {
			((BootstrapContextAware) bean).setBootstrapContext(this.bootstrapContext);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
