

package org.springframework.scheduling.concurrent;

import java.util.Properties;
import java.util.concurrent.Executor;
import javax.naming.NamingException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.jndi.JndiLocatorDelegate;
import org.springframework.jndi.JndiTemplate;
import org.springframework.lang.Nullable;

/**
 * JNDI-based variant of {@link ConcurrentTaskExecutor}, performing a default lookup for
 * JSR-236's "java:comp/DefaultManagedExecutorService" in a Java EE 7 environment.
 *
 * <p>Note: This class is not strictly JSR-236 based; it can work with any regular
 * {@link java.util.concurrent.Executor} that can be found in JNDI.
 * The actual adapting to {@link javax.enterprise.concurrent.ManagedExecutorService}
 * happens in the base class {@link ConcurrentTaskExecutor} itself.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
public class DefaultManagedTaskExecutor extends ConcurrentTaskExecutor implements InitializingBean {

	private JndiLocatorDelegate jndiLocator = new JndiLocatorDelegate();

	@Nullable
	private String jndiName = "java:comp/DefaultManagedExecutorService";


	/**
	 * Set the JNDI template to use for JNDI lookups.
	 * @see org.springframework.jndi.JndiAccessor#setJndiTemplate
	 */
	public void setJndiTemplate(JndiTemplate jndiTemplate) {
		this.jndiLocator.setJndiTemplate(jndiTemplate);
	}

	/**
	 * Set the JNDI environment to use for JNDI lookups.
	 * @see org.springframework.jndi.JndiAccessor#setJndiEnvironment
	 */
	public void setJndiEnvironment(Properties jndiEnvironment) {
		this.jndiLocator.setJndiEnvironment(jndiEnvironment);
	}

	/**
	 * Set whether the lookup occurs in a Java EE container, i.e. if the prefix
	 * "java:comp/env/" needs to be added if the JNDI name doesn't already
	 * contain it. PersistenceAnnotationBeanPostProcessor's default is "true".
	 * @see org.springframework.jndi.JndiLocatorSupport#setResourceRef
	 */
	public void setResourceRef(boolean resourceRef) {
		this.jndiLocator.setResourceRef(resourceRef);
	}

	/**
	 * Specify a JNDI name of the {@link java.util.concurrent.Executor} to delegate to,
	 * replacing the default JNDI name "java:comp/DefaultManagedExecutorService".
	 * <p>This can either be a fully qualified JNDI name, or the JNDI name relative
	 * to the current environment naming context if "resourceRef" is set to "true".
	 * @see #setConcurrentExecutor
	 * @see #setResourceRef
	 */
	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	@Override
	public void afterPropertiesSet() throws NamingException {
		if (this.jndiName != null) {
			setConcurrentExecutor(this.jndiLocator.lookup(this.jndiName, Executor.class));
		}
	}

}
