package com.goat.chapter115;


import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/1.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/1---9:41
 */
public class App  {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:application.xml");

	/** 抽象bean <bean abstract="true"/>  测试   会抛出异常 DefaultListableBeanFactory#805*/
	@Test
	public void test2(){
		Object abstractBeanTest = context.getBean("abstractBeanTest");
		System.out.println(abstractBeanTest);
	}
}
