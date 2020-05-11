

package org.springframework.jndi;

import javax.naming.NamingException;

import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;

/**
 * {@link PropertySource} implementation that reads properties from an underlying Spring {@link JndiLocatorDelegate}.
 *
 * By default, the underlying {@code JndiLocatorDelegate} will be configured with its
 * {@link JndiLocatorDelegate#setResourceRef(boolean) "resourceRef"} property set to
 * {@code true}, meaning that names looked up will automatically be prefixed with  "java:comp/env/" in alignment with published
 * <a href="https://download.oracle.com/javase/jndi/tutorial/beyond/misc/policy.html">JNDI
 * naming conventions</a>. To override this setting or to change the prefix, manually
 * configure a {@code JndiLocatorDelegate} and provide it to one of the constructors here
 * that accepts it. The same applies when providing custom JNDI properties. These should
 * be specified using {@link JndiLocatorDelegate#setJndiEnvironment(java.util.Properties)}  prior to construction of the {@code JndiPropertySource}.
 *
 * Note that {@link org.springframework.web.context.support.StandardServletEnvironment
 * StandardServletEnvironment} includes a {@code JndiPropertySource} by default, and any customization of the underlying {@link JndiLocatorDelegate} may be performed within an
 * {@link org.springframework.context.ApplicationContextInitializer ApplicationContextInitializer} or {@link org.springframework.web.WebApplicationInitializer WebApplicationInitializer}.
 * @since 3.1
 * @see JndiLocatorDelegate
 * @see org.springframework.context.ApplicationContextInitializer
 * @see org.springframework.web.WebApplicationInitializer
 * @see org.springframework.web.context.support.StandardServletEnvironment
 * JNDI：Java Naming and Directory Interface Java命名和目录接口。
 * 题外话：我认为它是IoC的鼻祖，在Tomcat中有大量的应用，Spring或许都是抄它的~
 */
public class JndiPropertySource extends PropertySource<JndiLocatorDelegate> {

	/**
	 * Create a new {@code JndiPropertySource} with the given name
	 * and a {@link JndiLocatorDelegate} configured to prefix any names with "java:comp/env/".
	 */
	public JndiPropertySource(String name) {
		this(name, JndiLocatorDelegate.createDefaultResourceRefLocator());
	}

	/**
	 * Create a new {@code JndiPropertySource} with the given name and the given {@code JndiLocatorDelegate}.
	 */
	public JndiPropertySource(String name, JndiLocatorDelegate jndiLocator) {
		super(name, jndiLocator);
	}

	/**
	 * This implementation looks up and returns the value associated with the given name from the underlying {@link JndiLocatorDelegate}.
	 * If a {@link NamingException} is thrown during the call to {@link JndiLocatorDelegate#lookup(String)},
	 * returns {@code null} and issues a DEBUG-level log statement with the exception message.
	 */
	@Override
	@Nullable
	public Object getProperty(String name) {
		if (getSource().isResourceRef() && name.indexOf(':') != -1) {
			// We're in resource-ref (prefixing with "java:comp/env") mode.
			// Let's not bother with property names with a colon it since they're probably just containing a default value clause,
			// very unlikely to match including the colon part even in  a textual property source,
			// and effectively never meant to match that way in  JNDI where a colon indicates a separator between JNDI scheme and actual name.
			return null;
		}
		try {
			Object value = this.source.lookup(name);
			if (logger.isDebugEnabled()) logger.debug("JNDI lookup for name [" + name + "] returned: [" + value + "]");
			return value;
		}catch (NamingException ex) {
			if (logger.isDebugEnabled()) logger.debug("JNDI lookup for name [" + name + "] threw NamingException " + "with message: " + ex.getMessage() + ". Returning null.");
			return null;
		}
	}
}
