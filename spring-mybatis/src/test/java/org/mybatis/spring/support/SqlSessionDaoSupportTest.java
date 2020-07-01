

package org.mybatis.spring.support;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.AbstractMyBatisSpringTest;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SqlSessionDaoSupportTest extends AbstractMyBatisSpringTest {
  private SqlSessionDaoSupport sqlSessionDaoSupport;

  private GenericApplicationContext applicationContext;

  @BeforeEach
  void setup() {
    sqlSessionDaoSupport = new MockSqlSessionDao();
  }

  @AfterEach
  void closeConnection() throws SQLException {
    connection.close();
  }

  @Test
  void testWithSqlSessionTemplate() {
    SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
    sqlSessionDaoSupport.setSqlSessionTemplate(sessionTemplate);
    sqlSessionDaoSupport.afterPropertiesSet();
    assertThat(sqlSessionDaoSupport.getSqlSession()).as("should store the Template").isEqualTo(sessionTemplate);
  }

  @Test
  void testWithSqlSessionFactory() {
    sqlSessionDaoSupport.setSqlSessionFactory(sqlSessionFactory);
    sqlSessionDaoSupport.afterPropertiesSet();
    assertThat(((SqlSessionTemplate) sqlSessionDaoSupport.getSqlSession()).getSqlSessionFactory()).as("should store the Factory").isEqualTo(sqlSessionFactory);
  }

  @Test
  void testWithBothFactoryAndTemplate() {
    SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
    sqlSessionDaoSupport.setSqlSessionTemplate(sessionTemplate);
    sqlSessionDaoSupport.setSqlSessionFactory(sqlSessionFactory);
    sqlSessionDaoSupport.afterPropertiesSet();

    assertThat(sqlSessionDaoSupport.getSqlSession()).as("should ignore the Factory").isEqualTo(sessionTemplate);
  }

  @Test
  void testWithNoFactoryOrSession() {
    assertThrows(IllegalArgumentException.class, sqlSessionDaoSupport::afterPropertiesSet);
  }

  @Test
  void testAutowireWithNoFactoryOrSession() {
    setupContext();
    assertThrows(BeanCreationException.class, this::startContext);
  }

  @Test
  void testAutowireWithTwoFactories() {
    setupContext();

    setupSqlSessionFactory("factory1");
    setupSqlSessionFactory("factory2");

    assertThrows(BeanCreationException.class, this::startContext);
  }

  private void setupContext() {
    applicationContext = new GenericApplicationContext();

    GenericBeanDefinition definition = new GenericBeanDefinition();
    definition.setBeanClass(MockSqlSessionDao.class);
    applicationContext.registerBeanDefinition("dao", definition);

    // add support for autowiring fields
    AnnotationConfigUtils.registerAnnotationConfigProcessors(applicationContext);
  }

  private void startContext() {
    applicationContext.refresh();
    applicationContext.start();

    sqlSessionDaoSupport = applicationContext.getBean(MockSqlSessionDao.class);
  }

  private void setupSqlSessionFactory(String name) {
    GenericBeanDefinition definition = new GenericBeanDefinition();
    definition.setBeanClass(SqlSessionFactoryBean.class);
    definition.getPropertyValues().add("dataSource", dataSource);

    applicationContext.registerBeanDefinition(name, definition);
  }

  private static final class MockSqlSessionDao extends SqlSessionDaoSupport {
  }
}
