package com.goat.chapter201.factorymethod;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class AliasTest {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:factory-method.xml");

	/**
	 * 测试 factory-method 可用于标识静态工厂的工厂方法（工厂方法是静态的）
	 * 对于非静态工厂，需要使用 factory-bean 和 factory-method 两个属性配合
	 */
	@Test
	public void test31(){
		System.out.println("staticHelloFactory -> " + context.getBean("staticHelloFactory"));
	}
}
