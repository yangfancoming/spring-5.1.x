package com.goat.chapter201.bean.item03;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class App {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:merged-beandefinition.xml");

	/**
	 * 由测试结果可以看出，hello-child 在未配置 class 的属性下也实例化成功了，表明它成功继承了父配置的 class 属性。
	 */
	@Test
	public void test31(){
		Hello hello = (Hello) context.getBean("hello");
		Hello helloChild = (Hello) context.getBean("hello-child");
		System.out.println("hello -> " + hello.getContent());
		System.out.println("hello-child -> " + helloChild.getContent());
	}
}
