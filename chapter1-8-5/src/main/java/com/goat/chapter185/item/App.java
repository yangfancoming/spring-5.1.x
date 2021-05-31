package com.goat.chapter185.item;


import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: TODO
 * @ author  山羊来了
 */
public class App {

	ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);

	@Test
	public void te(){
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}

	/**
	 * 通过 BeanFactoryPostProcessor 让容器使用指定的构造函来实例化对象
	 * @see MyBeanFactoryPostProcessor
	 */
	@Test
	public void test() {
		Person person = ac.getBean(Person.class);
		/**
		 * 1.Person对象中有@Autowire注解时dog对象会被自动注入
		 * 2.当去掉Person对象中的@Autowire注解时  dog对象为null
		 * 3.当去掉Person对象中的@Autowire注解时  设置为AUTOWIRE_BY_NAME时 dog对象会被自动注入
		 * @see Person
		 * @see MyBeanFactoryPostProcessor#postProcessBeanFactory(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
		*/
		System.out.println(person.getDog());

	}

}
