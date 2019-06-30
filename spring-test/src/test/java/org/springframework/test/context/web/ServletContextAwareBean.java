

package org.springframework.test.context.web;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

/**
 * Introduced to investigate claims in SPR-11145.
 *
 * @author Sam Brannen
 * @since 4.0.2
 */
public class ServletContextAwareBean implements ServletContextAware {

	protected ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
