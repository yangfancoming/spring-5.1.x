package org.mybatis.spring.submitted.autowire;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

class AutowireTest {

  ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:org/mybatis/spring/submitted/autowire/spring.xml");

  @Test
  public void test() {
    String[] str = context.getBeanDefinitionNames();
    Arrays.stream(str).forEach(x -> System.out.println("***---***	 " + x));
  }

  @Test
  void shouldReturnMapper() {
    FooMapper fooMapper = (FooMapper) context.getBean("fooMapper");
    assertThat(fooMapper).isNotNull();
    String s = fooMapper.executeFoo();
    System.out.println(s);

    BarMapper barMapper = (BarMapper) context.getBean("barMapper");
    assertThat(barMapper).isNotNull();
    String s1 = barMapper.executeBar();
    System.out.println(s1);
  }
}
