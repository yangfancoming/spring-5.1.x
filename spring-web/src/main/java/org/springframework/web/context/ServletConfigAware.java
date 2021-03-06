

package org.springframework.web.context;

import javax.servlet.ServletConfig;

import org.springframework.beans.factory.Aware;

/**
 * Interface to be implemented by any object that wishes to be notified of the
 * {@link ServletConfig} (typically determined by the {@link WebApplicationContext})
 * that it runs in.
 *
 * Note: Only satisfied if actually running within a Servlet-specific
 * WebApplicationContext. Otherwise, no ServletConfig will be set.
 * @since 2.0
 * @see ServletContextAware
 */
public interface ServletConfigAware extends Aware {

	/**
	 * Set the {@link ServletConfig} that this object runs in.
	 * Invoked after population of normal bean properties but before an init
	 * callback like InitializingBean's {@code afterPropertiesSet} or a
	 * custom init-method. Invoked after ApplicationContextAware's
	 * {@code setApplicationContext}.
	 * @param servletConfig the {@link ServletConfig} to be used by this object
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
	 */
	void setServletConfig(ServletConfig servletConfig);

}
