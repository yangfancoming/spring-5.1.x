

package org.springframework.jms.connection;

import javax.jms.Session;

/**
 * Subinterface of {@link javax.jms.Session} to be implemented by
 * Session proxies. Allows access to the underlying target Session.
 *

 * @since 2.0.4
 * @see TransactionAwareConnectionFactoryProxy
 * @see CachingConnectionFactory
 * @see ConnectionFactoryUtils#getTargetSession(javax.jms.Session)
 */
public interface SessionProxy extends Session {

	/**
	 * Return the target Session of this proxy.
	 * xmlBeanDefinitionReaderThis will typically be the native provider Session
	 * or a wrapper from a session pool.
	 * @return the underlying Session (never {@code null})
	 */
	Session getTargetSession();

}
