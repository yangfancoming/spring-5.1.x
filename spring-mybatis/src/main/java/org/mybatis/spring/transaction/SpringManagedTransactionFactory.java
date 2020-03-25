
package org.mybatis.spring.transaction;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * Creates a {@code SpringManagedTransaction}.
 */
public class SpringManagedTransactionFactory implements TransactionFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
    return new SpringManagedTransaction(dataSource);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Transaction newTransaction(Connection conn) {
    throw new UnsupportedOperationException("New Spring transactions require a DataSource");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setProperties(Properties props) {
    // not needed in this version
  }

}
