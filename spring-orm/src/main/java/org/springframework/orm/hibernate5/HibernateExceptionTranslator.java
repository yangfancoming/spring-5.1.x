

package org.springframework.orm.hibernate5;

import javax.persistence.PersistenceException;

import org.hibernate.HibernateException;
import org.hibernate.JDBCException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

/**
 * {@link PersistenceExceptionTranslator} capable of translating {@link HibernateException}
 * instances to Spring's {@link DataAccessException} hierarchy. As of Spring 4.3.2 and
 * Hibernate 5.2, it also converts standard JPA {@link PersistenceException} instances.
 *
 * xmlBeanDefinitionReaderExtended by {@link LocalSessionFactoryBean}, so there is no need to declare this
 * translator in addition to a {@code LocalSessionFactoryBean}.
 *
 * xmlBeanDefinitionReaderWhen configuring the container with {@code @Configuration} classes, a {@code @Bean}
 * of this type must be registered manually.
 *

 * @since 4.2
 * @see org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
 * @see SessionFactoryUtils#convertHibernateAccessException(HibernateException)
 * @see EntityManagerFactoryUtils#convertJpaAccessExceptionIfPossible(RuntimeException)
 */
public class HibernateExceptionTranslator implements PersistenceExceptionTranslator {

	@Nullable
	private SQLExceptionTranslator jdbcExceptionTranslator;


	/**
	 * Set the JDBC exception translator for Hibernate exception translation purposes.
	 * xmlBeanDefinitionReaderApplied to any detected {@link java.sql.SQLException} root cause of a Hibernate
	 * {@link JDBCException}, overriding Hibernate's own {@code SQLException} translation
	 * (which is based on a Hibernate Dialect for a specific target database).
	 * @since 5.1
	 * @see java.sql.SQLException
	 * @see org.hibernate.JDBCException
	 * @see org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
	 * @see org.springframework.jdbc.support.SQLStateSQLExceptionTranslator
	 */
	public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
		this.jdbcExceptionTranslator = jdbcExceptionTranslator;
	}


	@Override
	@Nullable
	public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
		if (ex instanceof HibernateException) {
			return convertHibernateAccessException((HibernateException) ex);
		}
		if (ex instanceof PersistenceException) {
			if (ex.getCause() instanceof HibernateException) {
				return convertHibernateAccessException((HibernateException) ex.getCause());
			}
			return EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
		}
		return null;
	}

	/**
	 * Convert the given HibernateException to an appropriate exception from the
	 * {@code org.springframework.dao} hierarchy.
	 * xmlBeanDefinitionReaderWill automatically apply a specified SQLExceptionTranslator to a
	 * Hibernate JDBCException, otherwise rely on Hibernate's default translation.
	 * @param ex the HibernateException that occurred
	 * @return a corresponding DataAccessException
	 * @see SessionFactoryUtils#convertHibernateAccessException
	 */
	protected DataAccessException convertHibernateAccessException(HibernateException ex) {
		if (this.jdbcExceptionTranslator != null && ex instanceof JDBCException) {
			JDBCException jdbcEx = (JDBCException) ex;
			DataAccessException dae = this.jdbcExceptionTranslator.translate(
					"Hibernate operation: " + jdbcEx.getMessage(), jdbcEx.getSQL(), jdbcEx.getSQLException());
			if (dae != null) {
				throw dae;
			}
		}
		return SessionFactoryUtils.convertHibernateAccessException(ex);
	}

}
