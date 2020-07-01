

package org.mybatis.spring.batch.builder;

import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.springframework.batch.item.ExecutionContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Tests for {@link MyBatisCursorItemReaderBuilder}.
 * @since 2.0.0
 * @author Kazuki Shimizu
 */
class MyBatisCursorItemReaderBuilderTest {

  @Mock
  private SqlSessionFactory sqlSessionFactory;

  @Mock
  private SqlSession sqlSession;

  @Mock
  private Cursor<Object> cursor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(this.sqlSessionFactory.openSession(ExecutorType.SIMPLE)).thenReturn(this.sqlSession);
    Mockito.when(this.cursor.iterator()).thenReturn(getFoos().iterator());
    Mockito.when(this.sqlSession.selectCursor("selectFoo", Collections.singletonMap("id", 1))).thenReturn(this.cursor);
  }

  @Test
  void testConfiguration() throws Exception {
    // @formatter:off
    MyBatisCursorItemReader<Foo> itemReader = new MyBatisCursorItemReaderBuilder<Foo>()
            .sqlSessionFactory(this.sqlSessionFactory)
            .queryId("selectFoo")
            .parameterValues(Collections.singletonMap("id", 1))
            .build();
    // @formatter:on
    itemReader.afterPropertiesSet();

    ExecutionContext executionContext = new ExecutionContext();
    itemReader.open(executionContext);

    Assertions.assertThat(itemReader.read()).extracting(Foo::getName).isEqualTo("foo1");
    Assertions.assertThat(itemReader.read()).extracting(Foo::getName).isEqualTo("foo2");
    Assertions.assertThat(itemReader.read()).extracting(Foo::getName).isEqualTo("foo3");

    itemReader.update(executionContext);
    Assertions.assertThat(executionContext.getInt("MyBatisCursorItemReader.read.count")).isEqualTo(3);
    Assertions.assertThat(executionContext.containsKey("MyBatisCursorItemReader.read.count.max")).isFalse();

    Assertions.assertThat(itemReader.read()).isNull();
  }

  @Test
  void testConfigurationSaveStateIsFalse() throws Exception {

    // @formatter:off
    MyBatisCursorItemReader<Foo> itemReader = new MyBatisCursorItemReaderBuilder<Foo>()
            .sqlSessionFactory(this.sqlSessionFactory)
            .queryId("selectFoo")
            .parameterValues(Collections.singletonMap("id", 1))
            .saveState(false)
            .build();
    // @formatter:on
    itemReader.afterPropertiesSet();

    ExecutionContext executionContext = new ExecutionContext();
    itemReader.open(executionContext);

    Assertions.assertThat(itemReader.read()).extracting(Foo::getName).isEqualTo("foo1");
    Assertions.assertThat(itemReader.read()).extracting(Foo::getName).isEqualTo("foo2");
    Assertions.assertThat(itemReader.read()).extracting(Foo::getName).isEqualTo("foo3");

    itemReader.update(executionContext);
    Assertions.assertThat(executionContext.isEmpty()).isTrue();

  }

  @Test
  void testConfigurationMaxItemCount() throws Exception {

    // @formatter:off
    MyBatisCursorItemReader<Foo> itemReader = new MyBatisCursorItemReaderBuilder<Foo>()
            .sqlSessionFactory(this.sqlSessionFactory)
            .queryId("selectFoo")
            .parameterValues(Collections.singletonMap("id", 1))
            .maxItemCount(2)
            .build();
    // @formatter:on
    itemReader.afterPropertiesSet();

    ExecutionContext executionContext = new ExecutionContext();
    itemReader.open(executionContext);

    Assertions.assertThat(itemReader.read()).extracting(Foo::getName).isEqualTo("foo1");
    Assertions.assertThat(itemReader.read()).extracting(Foo::getName).isEqualTo("foo2");

    itemReader.update(executionContext);
    Assertions.assertThat(executionContext.getInt("MyBatisCursorItemReader.read.count.max")).isEqualTo(2);

    Assertions.assertThat(itemReader.read()).isNull();
  }

  private List<Object> getFoos() {
    return Arrays.asList(new Foo("foo1"), new Foo("foo2"), new Foo("foo3"));
  }

  private static class Foo {
    private final String name;

    Foo(String name) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }
  }

}
