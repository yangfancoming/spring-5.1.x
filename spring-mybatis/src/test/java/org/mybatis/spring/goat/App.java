package org.mybatis.spring.goat;

import org.junit.Test;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

import static org.mockito.Mockito.mock;

/**
 * Created by Administrator on 2021/6/6.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/6---17:51
 */
public class App {

	BeanDefinitionRegistry beanDefinitionRegistry = mock(BeanDefinitionRegistry.class);

	String basePackages[] = new String[]{"123","223"};
	@Test
	public void test(){
		ClassPathMapperScanner zi = new ClassPathMapperScanner(beanDefinitionRegistry);
		zi.scan(basePackages); // 走子类
	}

	@Test
	public void test2(){
		ClassPathBeanDefinitionScanner fu = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);
		fu.scan(basePackages); // 走父类
	}
}
