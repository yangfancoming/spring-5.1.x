package com.goat.chapter400.annotation;

import com.goat.chapter400.annotation.service.BarService;
import com.goat.chapter400.annotation.service.HelloService;
import com.goat.chapter400.annotation.service.HelloServiceImpl;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@ComponentScan("com.goat.chapter400.annotation")
@EnableAspectJAutoProxy // 为什么必须要加 这个注解？
public class App {

	/**  aop 无效 没有交给Spring容器管理 */
	@Test
	public void test(){
		HelloServiceImpl helloService = new HelloServiceImpl();
		helloService.sayHiService1();
	}

	/**  aop 有效 */
	@Test
	public void test2(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		// JDK 代理类
		HelloService printer = context.getBean(HelloService.class);
		printer.sayHiService1();
	}

	/**  有实现接口的独立service类
	 * doit  为啥报错？  No qualifying bean of type 'com.goat.chapter400.annotation.service.HelloServiceImpl' available*/
	@Test
	public void test1(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		HelloServiceImpl printer = context.getBean(HelloServiceImpl.class);
		printer.sayHiService1();
	}



	/**  aop 有效  没有实现接口的独立service类  通过Bean名称 方式 getBean */
	@Test
	public void test3(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		// CGLIB 代理列
		BarService printer = (BarService) context.getBean("barService");
		printer.test();
	}

	/**  aop 有效  没有实现接口的独立service类  通过class方式 getBean */
	@Test
	public void test12(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		BarService printer = context.getBean(BarService.class);
		printer.test();
	}






}
