

package org.springframework.orm.hibernate5;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import org.springframework.lang.Nullable;

/**
 * Callback interface for Hibernate code. To be used with {@link HibernateTemplate}'s
 * execution methods, often as anonymous classes within a method implementation.
 * A typical implementation will call {@code Session.load/find/update} to perform
 * some operations on persistent objects.
 *

 * @since 4.2
 * @param <T> the result type
 * @see HibernateTemplate
 * @see HibernateTransactionManager
 */
@FunctionalInterface
public interface HibernateCallback<T> {

	/**
	 * Gets called by {@code HibernateTemplate.execute} with an active
	 * Hibernate {@code Session}. Does not need to care about activating
	 * or closing the {@code Session}, or handling transactions.
	 * xmlBeanDefinitionReaderAllows for returning a result object created within the callback,
	 * i.e. a domain object or a collection of domain objects.
	 * A thrown custom RuntimeException is treated as an application exception:
	 * It gets propagated to the caller of the template.
	 * @param session active Hibernate session
	 * @return a result object, or {@code null} if none
	 * @throws HibernateException if thrown by the Hibernate API
	 * @see HibernateTemplate#execute
	 */
	@Nullable
	T doInHibernate(Session session) throws HibernateException;

}
