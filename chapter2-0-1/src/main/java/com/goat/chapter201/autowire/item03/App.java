package com.goat.chapter201.autowire.item03;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 自动注入  注解版
 */

@ComponentScan("com.goat.chapter201.autowire.item03")
public class App {

	@Test
	public void test(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(App.class);
		Service bean = ac.getBean(Service.class);
		System.out.println(bean);
	}
}