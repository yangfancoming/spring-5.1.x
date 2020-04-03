

package org.springframework.orm.hibernate5;

import org.hibernate.HibernateException;

import org.springframework.dao.UncategorizedDataAccessException;
import org.springframework.lang.Nullable;

/**
 * Hibernate-specific subclass of UncategorizedDataAccessException,
 * for Hibernate system errors that do not match any concrete
 * {@code org.springframework.dao} exceptions.
 *

 * @since 4.2
 * @see SessionFactoryUtils#convertHibernateAccessException
 */
@SuppressWarnings("serial")
public class HibernateSystemException extends UncategorizedDataAccessException {

	/**
	 * Create a new HibernateSystemException,
	 * wrapping an arbitrary HibernateException.
	 * @param cause the HibernateException thrown
	 */
	public HibernateSystemException(@Nullable HibernateException cause) {
		super(cause != null ? cause.getMessage() : null, cause);
	}

}
