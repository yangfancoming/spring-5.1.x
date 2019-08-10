package com.goat.chapter400.annotation;

import com.goat.chapter400.annotation.service.BarService;
import com.goat.chapter400.annotation.service.HelloService;
import com.goat.chapter400.annotation.service.HelloServiceImpl;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 通知 spring 开启切面功能的两种方式：
 * 1. @EnableAspectJAutoProxy  注解版
 * 2. <aop:aspectj-autoproxy/> xml版
 */
@ComponentScan("com.goat.chapter400.annotation")
@EnableAspectJAutoProxy
public class App {

	/**  aop 无效  自己new出来的没有交给Spring容器管理 */
	@Test
	public void test(){
		HelloServiceImpl helloService = new HelloServiceImpl();
		helloService.sayHiService1("11");
	}

	/**  aop 有效  从Spring容器中获取的bean*/
	@Test
	public void test2(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		// JDK 代理类
		HelloService printer = context.getBean(HelloService.class);
		String s = printer.sayHiService1("123");
		System.out.println(s); // sos 正常返回增强 获取到的返回值为null 是因为环绕增强改变了返回值
	}

	/**  测试异常增强 */
	@Test
	public void test33(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		HelloService printer = context.getBean(HelloService.class);
		String s = printer.sayHiService2();
		System.out.println(s); //
	}

	/**  有实现接口的独立service类
	 * doit  为啥报错？  No qualifying bean of type 'com.goat.chapter400.annotation.service.HelloServiceImpl' available*/
	@Test
	public void test1(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		HelloServiceImpl printer = context.getBean(HelloServiceImpl.class);
		printer.sayHiService1("123");
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
