
package org.mybatis.spring;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.transaction.TransactionException;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Default exception translator.
 * Translates MyBatis SqlSession returned exception into a Spring {@code DataAccessException} using Spring's
 * {@code SQLExceptionTranslator} Can load {@code SQLExceptionTranslator} eagerly or when the first exception is
 * translated.
 */
public class MyBatisExceptionTranslator implements PersistenceExceptionTranslator {

  private final DataSource dataSource;

  private SQLExceptionTranslator exceptionTranslator;

  /**
   * Creates a new {@code DataAccessExceptionTranslator} instance.
   * @param dataSource DataSource to use to find metadata and establish which error codes are usable.
   * @param exceptionTranslatorLazyInit if true, the translator instantiates internal stuff only the first time will have the need to translate  exceptions.
   */
  public MyBatisExceptionTranslator(DataSource dataSource, boolean exceptionTranslatorLazyInit) {
    this.dataSource = dataSource;
    if (!exceptionTranslatorLazyInit) {
      this.initExceptionTranslator();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DataAccessException translateExceptionIfPossible(RuntimeException e) {
    if (e instanceof PersistenceException) {
      // Batch exceptions come inside another PersistenceException
      // recursion has a risk of infinite loop so better make another if
      if (e.getCause() instanceof PersistenceException) {
        e = (PersistenceException) e.getCause();
      }
      if (e.getCause() instanceof SQLException) {
        this.initExceptionTranslator();
        return this.exceptionTranslator.translate(e.getMessage() + "\n", null, (SQLException) e.getCause());
      } else if (e.getCause() instanceof TransactionException) {
        throw (TransactionException) e.getCause();
      }
      return new MyBatisSystemException(e);
    }
    return null;
  }

  /**
   * Initializes the internal translator reference.
   */
  private synchronized void initExceptionTranslator() {
    if (this.exceptionTranslator == null) {
      this.exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
    }
  }

}
