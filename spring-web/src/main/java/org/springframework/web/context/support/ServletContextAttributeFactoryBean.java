

package org.springframework.web.context.support;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.web.context.ServletContextAware;

/**
 * {@link FactoryBean} that fetches a specific, existing ServletContext attribute.
 * Exposes that ServletContext attribute when used as bean reference,effectively making it available as named Spring bean instance.
 * Intended to link in ServletContext attributes that exist before
 * the startup of the Spring application context. Typically, such attributes will have been put there by third-party web frameworks.
 * In a purely Spring-based web application, no such linking in of ServletContext attributes will be necessary.
 * <b>NOTE:</b> As of Spring 3.0, you may also use the "contextAttributes" default bean which is of type Map,
 * and dereference it using an "#{contextAttributes.myKey}" expression to access a specific attribute by name.
 * @since 1.1.4
 * @see org.springframework.web.context.WebApplicationContext#CONTEXT_ATTRIBUTES_BEAN_NAME
 * @see ServletContextParameterFactoryBean
 */
public class ServletContextAttributeFactoryBean implements FactoryBean<Object>, ServletContextAware {

	@Nullable
	private String attributeName;

	@Nullable
	private Object attribute;

	/**
	 * Set the name of the ServletContext attribute to expose.
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		if (this.attributeName == null) {
			throw new IllegalArgumentException("Property 'attributeName' is required");
		}
		this.attribute = servletContext.getAttribute(this.attributeName);
		if (this.attribute == null) {
			throw new IllegalStateException("No ServletContext attribute '" + this.attributeName + "' found");
		}
	}


	@Override
	@Nullable
	public Object getObject() throws Exception {
		return this.attribute;
	}

	@Override
	public Class<?> getObjectType() {
		return (this.attribute != null ? this.attribute.getClass() : null);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
