

package org.springframework.web.context.support;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * Convenient base class for self-autowiring classes that gets constructed
 * within a Spring-based web application. Resolves {@code @Autowired}
 * annotations in the endpoint class against beans in the current Spring
 * root web application context (as determined by the current thread's
 * context ClassLoader, which needs to be the web application's ClassLoader).
 * Can alternatively be used as a delegate instead of as a base class.
 *
 * A typical usage of this base class is a JAX-WS endpoint class:
 * Such a Spring-based JAX-WS endpoint implementation will follow the
 * standard JAX-WS contract for endpoint classes but will be 'thin'
 * in that it delegates the actual work to one or more Spring-managed
 * service beans - typically obtained using {@code @Autowired}.
 * The lifecycle of such an endpoint instance will be managed by the
 * JAX-WS runtime, hence the need for this base class to provide
 * {@code @Autowired} processing based on the current Spring context.
 *
 * <b>NOTE:</b> If there is an explicit way to access the ServletContext,
 * prefer such a way over using this class. The {@link WebApplicationContextUtils}
 * class allows for easy access to the Spring root web application context
 * based on the ServletContext.
 *

 * @since 2.5.1
 * @see WebApplicationObjectSupport
 */
public abstract class SpringBeanAutowiringSupport {

	private static final Log logger = LogFactory.getLog(SpringBeanAutowiringSupport.class);


	/**
	 * This constructor performs injection on this instance,
	 * based on the current web application context.
	 * Intended for use as a base class.
	 * @see #processInjectionBasedOnCurrentContext
	 */
	public SpringBeanAutowiringSupport() {
		processInjectionBasedOnCurrentContext(this);
	}


	/**
	 * Process {@code @Autowired} injection for the given target object,
	 * based on the current web application context.
	 * Intended for use as a delegate.
	 * @param target the target object to process
	 * @see org.springframework.web.context.ContextLoader#getCurrentWebApplicationContext()
	 */
	public static void processInjectionBasedOnCurrentContext(Object target) {
		Assert.notNull(target, "Target object must not be null");
		WebApplicationContext cc = ContextLoader.getCurrentWebApplicationContext();
		if (cc != null) {
			AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
			bpp.setBeanFactory(cc.getAutowireCapableBeanFactory());
			bpp.processInjection(target);
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("Current WebApplicationContext is not available for processing of " +
						ClassUtils.getShortName(target.getClass()) + ": " +
						"Make sure this class gets constructed in a Spring web application. Proceeding without injection.");
			}
		}
	}


	/**
	 * Process {@code @Autowired} injection for the given target object,
	 * based on the current root web application context as stored in the ServletContext.
	 * Intended for use as a delegate.
	 * @param target the target object to process
	 * @param servletContext the ServletContext to find the Spring web application context in
	 * @see WebApplicationContextUtils#getWebApplicationContext(javax.servlet.ServletContext)
	 */
	public static void processInjectionBasedOnServletContext(Object target, ServletContext servletContext) {
		Assert.notNull(target, "Target object must not be null");
		WebApplicationContext cc = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
		bpp.setBeanFactory(cc.getAutowireCapableBeanFactory());
		bpp.processInjection(target);
	}

}
