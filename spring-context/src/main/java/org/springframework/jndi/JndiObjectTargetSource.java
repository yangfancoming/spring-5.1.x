

package org.springframework.jndi;

import javax.naming.NamingException;

import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;

/**
 * AOP {@link org.springframework.aop.TargetSource} that provides
 * configurable JNDI lookups for {@code getTarget()} calls.
 *
 * Can be used as alternative to {@link JndiObjectFactoryBean}, to allow for
 * relocating a JNDI object lazily or for each operation (see "lookupOnStartup"
 * and "cache" properties). This is particularly useful during development, as it
 * allows for hot restarting of the JNDI server (for example, a remote JMS server).
 *
 * Example:
 *
 * <pre class="code">
 * &lt;bean id="queueConnectionFactoryTarget" class="org.springframework.jndi.JndiObjectTargetSource"&gt;
 *   &lt;property name="jndiName" value="JmsQueueConnectionFactory"/&gt;
 *   &lt;property name="lookupOnStartup" value="false"/&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id="queueConnectionFactory" class="org.springframework.aop.framework.ProxyFactoryBean"&gt;
 *   &lt;property name="proxyInterfaces" value="javax.jms.QueueConnectionFactory"/&gt;
 *   &lt;property name="targetSource" ref="queueConnectionFactoryTarget"/&gt;
 * &lt;/bean&gt;</pre>
 *
 * A {@code createQueueConnection} call on the "queueConnectionFactory" proxy will
 * cause a lazy JNDI lookup for "JmsQueueConnectionFactory" and a subsequent delegating
 * call to the retrieved QueueConnectionFactory's {@code createQueueConnection}.
 *
 * <b>Alternatively, use a {@link JndiObjectFactoryBean} with a "proxyInterface".</b>
 * "lookupOnStartup" and "cache" can then be specified on the JndiObjectFactoryBean,
 * creating a JndiObjectTargetSource underneath (instead of defining separate
 * ProxyFactoryBean and JndiObjectTargetSource beans).
 *

 * @since 1.1
 * @see #setLookupOnStartup
 * @see #setCache
 * @see org.springframework.aop.framework.ProxyFactoryBean#setTargetSource
 * @see JndiObjectFactoryBean#setProxyInterface
 */
public class JndiObjectTargetSource extends JndiObjectLocator implements TargetSource {

	private boolean lookupOnStartup = true;

	private boolean cache = true;

	@Nullable
	private Object cachedObject;

	@Nullable
	private Class<?> targetClass;


	/**
	 * Set whether to look up the JNDI object on startup. Default is "true".
	 * Can be turned off to allow for late availability of the JNDI object.
	 * In this case, the JNDI object will be fetched on first access.
	 * @see #setCache
	 */
	public void setLookupOnStartup(boolean lookupOnStartup) {
		this.lookupOnStartup = lookupOnStartup;
	}

	/**
	 * Set whether to cache the JNDI object once it has been located.
	 * Default is "true".
	 * Can be turned off to allow for hot redeployment of JNDI objects.
	 * In this case, the JNDI object will be fetched for each invocation.
	 * @see #setLookupOnStartup
	 */
	public void setCache(boolean cache) {
		this.cache = cache;
	}

	@Override
	public void afterPropertiesSet() throws NamingException {
		super.afterPropertiesSet();
		if (this.lookupOnStartup) {
			Object object = lookup();
			if (this.cache) {
				this.cachedObject = object;
			}
			else {
				this.targetClass = object.getClass();
			}
		}
	}


	@Override
	@Nullable
	public Class<?> getTargetClass() {
		if (this.cachedObject != null) {
			return this.cachedObject.getClass();
		}
		else if (this.targetClass != null) {
			return this.targetClass;
		}
		else {
			return getExpectedType();
		}
	}

	@Override
	public boolean isStatic() {
		return (this.cachedObject != null);
	}

	@Override
	@Nullable
	public Object getTarget() {
		try {
			if (this.lookupOnStartup || !this.cache) {
				return (this.cachedObject != null ? this.cachedObject : lookup());
			}
			else {
				synchronized (this) {
					if (this.cachedObject == null) {
						this.cachedObject = lookup();
					}
					return this.cachedObject;
				}
			}
		}
		catch (NamingException ex) {
			throw new JndiLookupFailureException("JndiObjectTargetSource failed to obtain new target object", ex);
		}
	}

	@Override
	public void releaseTarget(Object target) {
	}

}
