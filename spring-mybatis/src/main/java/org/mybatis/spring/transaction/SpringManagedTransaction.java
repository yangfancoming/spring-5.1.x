
package org.mybatis.spring.transaction;

import org.apache.ibatis.transaction.Transaction;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.springframework.util.Assert.notNull;

/**
 * {@code SpringManagedTransaction} handles the lifecycle of a JDBC connection.
 * It retrieves a connection from Spring's transaction manager and returns it back to it when it is no longer needed.
 * If Spring's transaction handling is active it will no-op all commit/rollback/close calls assuming that the Spring transaction manager will do the job.
 * If it is not it will behave like {@code JdbcTransaction}.
 */
public class SpringManagedTransaction implements Transaction {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringManagedTransaction.class);

  private final DataSource dataSource;

  private Connection connection;

  private boolean isConnectionTransactional;

  private boolean autoCommit;

  public SpringManagedTransaction(DataSource dataSource) {
    notNull(dataSource, "No DataSource specified");
    this.dataSource = dataSource;
  }

  @Override
  public Connection getConnection() throws SQLException {
  	// 获取spring数据库连接
    if (connection == null) openConnection();
    return connection;
  }

  /**
   * Gets a connection from Spring transaction manager and discovers if this {@code Transaction} should manage connection or let it to Spring.
   * It also reads autocommit setting because when using Spring Transaction MyBatis thinks that autocommit is always
   * false and will always call commit/rollback so we need to no-op that calls.
   */
  private void openConnection() throws SQLException {
    connection = DataSourceUtils.getConnection(dataSource);// 获取spring数据库连接
    autoCommit = connection.getAutoCommit();
    isConnectionTransactional = DataSourceUtils.isConnectionTransactional(connection, dataSource);
    LOGGER.debug(() -> "JDBC Connection [" + connection + "] will" + (isConnectionTransactional ? " " : " not ") + "be managed by Spring");
  }

  @Override
  public void commit() throws SQLException {
    if (connection != null && !isConnectionTransactional && !autoCommit) {
      LOGGER.debug(() -> "Committing JDBC Connection [" + connection + "]");
      connection.commit();
    }
  }

  @Override
  public void rollback() throws SQLException {
    if (connection != null && !isConnectionTransactional && !autoCommit) {
      LOGGER.debug(() -> "Rolling back JDBC Connection [" + connection + "]");
      connection.rollback();
    }
  }

  @Override
  public void close() {
    DataSourceUtils.releaseConnection(connection, dataSource);
  }

  @Override
  public Integer getTimeout() {
    ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
    if (holder != null && holder.hasTimeout()) {
      return holder.getTimeToLiveInSeconds();
    }
    return null;
  }

}
