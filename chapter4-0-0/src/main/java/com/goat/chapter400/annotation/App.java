package com.goat.chapter400.annotation;

import com.goat.chapter400.annotation.service.HelloService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@ComponentScan("com.goat.chapter400.annotation")
@EnableAspectJAutoProxy // 为什么必须要加 这个注解？
public class App {

	/**  aop 无效 */
	@Test
	public void test(){
		HelloService helloService = new HelloService();
		helloService.sayHiService1();
	}

	/**  aop 有效 */
	@Test
	public void test1(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		HelloService printer = context.getBean(HelloService.class);
		printer.sayHiService1();
	}
}
