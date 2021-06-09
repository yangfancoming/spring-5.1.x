
package org.mybatis.spring;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.*;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.dao.support.PersistenceExceptionTranslator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;
import static org.apache.ibatis.reflection.ExceptionUtil.unwrapThrowable;
import static org.mybatis.spring.SqlSessionUtils.*;
import static org.springframework.util.Assert.notNull;

/**
 * Thread safe, Spring managed, {@code SqlSession} that works with Spring transaction management to ensure that that the
 * actual SqlSession used is the one associated with the current Spring transaction. In addition, it manages the session
 * life-cycle, including closing, committing or rolling back the session as necessary based on the Spring transaction configuration.
 * The template needs a SqlSessionFactory to create SqlSessions, passed as a constructor argument. It also can be
 * constructed indicating the executor type to be used, if not, the default executor type, defined in the session factory will be used.
 * This template converts MyBatis PersistenceExceptions into unchecked DataAccessExceptions, using, by default, a {@code MyBatisExceptionTranslator}.
 * Because SqlSessionTemplate is thread safe, a single instance can be shared by all DAOs; there should also be a small
 * memory savings by doing this. This pattern can be used in Spring configuration files as follows:
 * <pre class="code">
 * {@code
 * <bean id="sqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
 *   <constructor-arg ref="sqlSessionFactory" />
 * </bean>
 * }
 * </pre>
 * @see SqlSessionFactory
 * @see MyBatisExceptionTranslator
 */
public class SqlSessionTemplate implements SqlSession, DisposableBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionTemplate.class);

	private final SqlSessionFactory sqlSessionFactory;

	private final ExecutorType executorType;

	private final SqlSession sqlSessionProxy;

	private final PersistenceExceptionTranslator exceptionTranslator;

	/**
	 * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory} provided as an argument.
	 * @param sqlSessionFactory a factory of SqlSession
	 */
	public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		this(sqlSessionFactory, sqlSessionFactory.getConfiguration().getDefaultExecutorType());
	}

	/**
	 * Constructs a Spring managed SqlSession with the {@code SqlSessionFactory} provided as an argument and the given
	 * {@code ExecutorType} {@code ExecutorType} cannot be changed once the {@code SqlSessionTemplate} is constructed.
	 * @param sqlSessionFactory a factory of SqlSession
	 * @param executorType an executor type on session
	 */
	public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType) {
		this(sqlSessionFactory, executorType,new MyBatisExceptionTranslator(sqlSessionFactory.getConfiguration().getEnvironment().getDataSource(), true));
	}

	/**
	 * Constructs a Spring managed {@code SqlSession} with the given {@code SqlSessionFactory} and {@code ExecutorType}. A
	 * custom {@code SQLExceptionTranslator} can be provided as an argument so any {@code PersistenceException} thrown by
	 * MyBatis can be custom translated to a {@code RuntimeException} The {@code SQLExceptionTranslator} can also be null
	 * and thus no exception translation will be done and MyBatis exceptions will be thrown
	 *
	 * @param sqlSessionFactory  a factory of SqlSession
	 * @param executorType  an executor type on session
	 * @param exceptionTranslator a translator of exception
	 */
	public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory, ExecutorType executorType, PersistenceExceptionTranslator exceptionTranslator) {
		notNull(sqlSessionFactory, "Property 'sqlSessionFactory' is required");
		notNull(executorType, "Property 'executorType' is required");
		this.sqlSessionFactory = sqlSessionFactory;
		this.executorType = executorType;
		this.exceptionTranslator = exceptionTranslator;
		// 创建sqlSession代理
		this.sqlSessionProxy = (SqlSession) newProxyInstance(SqlSessionFactory.class.getClassLoader(), new Class[] { SqlSession.class }, new SqlSessionInterceptor());
		LOGGER.warn(() -> "【mybatis】 构建 SqlSessionTemplate 完毕 ： " + sqlSessionProxy);
	}

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public ExecutorType getExecutorType() {
		return executorType;
	}

	public PersistenceExceptionTranslator getPersistenceExceptionTranslator() {
		return exceptionTranslator;
	}

	@Override
	public <T> T selectOne(String statement) {
		return sqlSessionProxy.selectOne(statement);
	}

	@Override
	public <T> T selectOne(String statement, Object parameter) {
		return sqlSessionProxy.selectOne(statement, parameter);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		return sqlSessionProxy.selectMap(statement, mapKey);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		return sqlSessionProxy.selectMap(statement, parameter, mapKey);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
		return sqlSessionProxy.selectMap(statement, parameter, mapKey, rowBounds);
	}

	@Override
	public <T> Cursor<T> selectCursor(String statement) {
		return sqlSessionProxy.selectCursor(statement);
	}

	@Override
	public <T> Cursor<T> selectCursor(String statement, Object parameter) {
		return sqlSessionProxy.selectCursor(statement, parameter);
	}

	@Override
	public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
		return sqlSessionProxy.selectCursor(statement, parameter, rowBounds);
	}

	@Override
	public <E> List<E> selectList(String statement) {
		return sqlSessionProxy.selectList(statement);
	}

	@Override
	public <E> List<E> selectList(String statement, Object parameter) {
		return sqlSessionProxy.selectList(statement, parameter);
	}

	@Override
	public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
		return sqlSessionProxy.selectList(statement, parameter, rowBounds);
	}

	@Override
	public void select(String statement, ResultHandler handler) {
		sqlSessionProxy.select(statement, handler);
	}

	@Override
	public void select(String statement, Object parameter, ResultHandler handler) {
		sqlSessionProxy.select(statement, parameter, handler);
	}

	@Override
	public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
		sqlSessionProxy.select(statement, parameter, rowBounds, handler);
	}

	@Override
	public int insert(String statement) {
		return sqlSessionProxy.insert(statement);
	}

	@Override
	public int insert(String statement, Object parameter) {
		return sqlSessionProxy.insert(statement, parameter);
	}

	@Override
	public int update(String statement) {
		return sqlSessionProxy.update(statement);
	}

	@Override
	public int update(String statement, Object parameter) {
		return sqlSessionProxy.update(statement, parameter);
	}

	@Override
	public int delete(String statement) {
		return sqlSessionProxy.delete(statement);
	}

	@Override
	public int delete(String statement, Object parameter) {
		return sqlSessionProxy.delete(statement, parameter);
	}

	@Override
	public <T> T getMapper(Class<T> type) {
		return getConfiguration().getMapper(type, this);
	}

	@Override
	public void commit() {
		throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void commit(boolean force) {
		throw new UnsupportedOperationException("Manual commit is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void rollback() {
		throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void rollback(boolean force) {
		throw new UnsupportedOperationException("Manual rollback is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException("Manual close is not allowed over a Spring managed SqlSession");
	}

	@Override
	public void clearCache() {
		sqlSessionProxy.clearCache();
	}

	@Override
	public Configuration getConfiguration() {
		return sqlSessionFactory.getConfiguration();
	}

	@Override
	public Connection getConnection() {
		return sqlSessionProxy.getConnection();
	}

	/**
	 * @since 1.0.2
	 */
	@Override
	public List<BatchResult> flushStatements() {
		return sqlSessionProxy.flushStatements();
	}

	/**
	 * Allow gently dispose bean:
	 * <pre>
	 * {@code
	 * <bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
	 *  <constructor-arg index="0" ref="sqlSessionFactory" />
	 * </bean>
	 * }
	 * </pre>
	 * The implementation of {@link DisposableBean} forces spring context to use {@link DisposableBean#destroy()} method
	 * instead of {@link SqlSessionTemplate#close()} to shutdown gently.
	 *
	 * @see SqlSessionTemplate#close()
	 * @see "org.springframework.beans.factory.support.DisposableBeanAdapter#inferDestroyMethodIfNecessary(Object, RootBeanDefinition)"
	 * @see "org.springframework.beans.factory.support.DisposableBeanAdapter#CLOSE_METHOD_NAME"
	 */
	@Override
	public void destroy() {
		// This method forces spring disposer to avoid call of SqlSessionTemplate.close() which gives
		// UnsupportedOperationException
	}

	/**
	 * Proxy needed to route MyBatis method calls to the proper SqlSession got from Spring's Transaction Manager It also
	 * unwraps exceptions thrown by {@code Method#invoke(Object, Object...)} to pass a {@code PersistenceException} to the
	 * {@code PersistenceExceptionTranslator}.
	 */
	private class SqlSessionInterceptor implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			/* 获取sqlSession */
			SqlSession sqlSession = getSqlSession(SqlSessionTemplate.this.sqlSessionFactory, SqlSessionTemplate.this.executorType, SqlSessionTemplate.this.exceptionTranslator);
			try {
				// 执行sqlSession的对应方法
				Object result = method.invoke(sqlSession, args);
				// 如果sqlSession不是由Spring管理的，则提交sqlSession
				if (!isSqlSessionTransactional(sqlSession, SqlSessionTemplate.this.sqlSessionFactory)) {
					// force commit even on non-dirty sessions because some databases require  a commit/rollback before calling close()
					// 强制提交sqlSession，因为一些数据库在调用close方法之前需要提交/回滚。
					sqlSession.commit(true);
				}
				return result;
			} catch (Throwable t) {
				Throwable unwrapped = unwrapThrowable(t);
				if (SqlSessionTemplate.this.exceptionTranslator != null && unwrapped instanceof PersistenceException) {
					// release the connection to avoid a deadlock if the translator is no loaded. See issue #22
					closeSqlSession(sqlSession, SqlSessionTemplate.this.sqlSessionFactory);
					sqlSession = null;
					Throwable translated = SqlSessionTemplate.this.exceptionTranslator.translateExceptionIfPossible((PersistenceException) unwrapped);
					if (translated != null) {
						unwrapped = translated;
					}
				}
				throw unwrapped;
			} finally {
				if (sqlSession != null) {
					closeSqlSession(sqlSession, SqlSessionTemplate.this.sqlSessionFactory);
				}
			}
		}
	}

}
