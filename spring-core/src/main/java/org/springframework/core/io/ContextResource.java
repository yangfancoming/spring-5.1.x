

package org.springframework.core.io;

/**
 * Extended interface for a resource that is loaded from an enclosing 'context',
 * e.g. from a {@link javax.servlet.ServletContext} but also from plain classpath paths or relative file system paths
 * (specified without an explicit prefix, hence applying relative to the local {@link ResourceLoader}'s context).
 * @since 2.5
 * @see org.springframework.web.context.support.ServletContextResource
 */
public interface ContextResource extends Resource {

	/**
	 * Return the path within the enclosing 'context'.
	 * This is typically path relative to a context-specific root directory,e.g. a ServletContext root or a PortletContext root.
	 */
	String getPathWithinContext();

}
