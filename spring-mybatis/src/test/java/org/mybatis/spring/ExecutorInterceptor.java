

package org.mybatis.spring;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.plugin.*;

import java.util.Properties;

/**
 * Keeps track of Executor commits, rollbacks and close status.
 *
 * The Executor is not accessible from DefaultSqlSession, so it is much easier to use an Interceptor rather than
 * subclass a new SqlSessionFactory, etc. Also, there is the potential to change the default MyBatis behavior, which
 * could change the test results.
 */
@Intercepts({ @Signature(type = Executor.class, method = "commit", args = { boolean.class }),
    @Signature(type = Executor.class, method = "rollback", args = { boolean.class }),
    @Signature(type = Executor.class, method = "close", args = { boolean.class }) })
final class ExecutorInterceptor implements Interceptor {

  private int commitCount;

  private int rollbackCount;

  private boolean closed;

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    if ("commit".equals(invocation.getMethod().getName())) {
      ++this.commitCount;
    } else if ("rollback".equals(invocation.getMethod().getName())) {
      ++this.rollbackCount;
    } else if ("close".equals(invocation.getMethod().getName())) {
      this.closed = true;
    }

    return invocation.proceed();
  }

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {
    // do nothing
  }

  void reset() {
    this.commitCount = 0;
    this.rollbackCount = 0;
    this.closed = false;
  }

  int getCommitCount() {
    return this.commitCount;
  }

  int getRollbackCount() {
    return this.rollbackCount;
  }

  boolean isExecutorClosed() {
    return this.closed;
  }

}
