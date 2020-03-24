package com.goat.chapter400.annotation;

import com.goat.chapter400.annotation.service.BarService;
import com.goat.chapter400.annotation.service.HelloService;
import com.goat.chapter400.annotation.service.HelloServiceImpl;
import com.goat.chapter400.annotation.service.IAddition;
import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.lang.reflect.Method;

import static junit.framework.TestCase.*;

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

	/** JDK 代理类  aop 有效 实现接口service类  从Spring容器中获取的bean*/
	@Test
	public void test2() {
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		HelloService printer = context.getBean(HelloService.class);
		String s = printer.sayHiService1("123");
		System.out.println(s); // sos 正常返回增强 获取到的返回值为null 是因为环绕增强改变了返回值
		// 判断是否为代理对象
		assertTrue(AopUtils.isAopProxy(printer));
		// 判断是否为jdk代理对象
		assertTrue(AopUtils.isJdkDynamicProxy(printer));
		// 判断是否为cglib代理对象
		assertFalse(AopUtils.isCglibProxy(printer));
		// 获取对象的真实类型
		Class<?> targetClass = AopUtils.getTargetClass(printer);
		assertEquals(HelloServiceImpl.class,targetClass);
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


	/**   CGLIB 代理类 aop 有效  没有实现接口的独立service类  通过Bean名称 方式 getBean */
	@Test
	public void test3() throws NoSuchMethodException {
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		BarService printer = (BarService) context.getBean("barService");
		printer.test();
		// 判断是否为代理对象
		assertTrue(AopUtils.isAopProxy(printer));
		// 判断是否为jdk代理对象
		assertFalse(AopUtils.isJdkDynamicProxy(printer));
		// 判断是否为cglib代理对象
		assertTrue(AopUtils.isCglibProxy(printer));
		// 获取对象的真实类型
		Class<?> targetClass = AopUtils.getTargetClass(printer);
		assertEquals(BarService.class,targetClass);

		/**
		 * m是通过代理对象的class得到的test方法，从打印结果可以非常明显的看出，
		 * 这个method隶属于com.goat.chapter400.annotation.service.BarService$$EnhancerBySpringCGLIB$$258824a0
		 * 那么使用这个方法肯定是没法去执行真实BarService的test方法，
		*/
		Method m = printer.getClass().getMethod("test");
		// public final void com.goat.chapter400.annotation.service.BarService$$EnhancerBySpringCGLIB$$258824a0.test()
		System.out.println(m);
		Method om = AopUtils.getMostSpecificMethod(m,AopUtils.getTargetClass(printer));
		// public void com.goat.chapter400.annotation.service.BarService.test()
		System.out.println(om);
	}

	/**  aop 有效  没有实现接口的独立service类  通过class方式 getBean */
	@Test
	public void test12(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		BarService printer = context.getBean(BarService.class);
		printer.test();
	}

	// 判断一个切入点是否匹配一个类型
	@Test
	public void testApply(){
		AspectJExpressionPointcut pc = new AspectJExpressionPointcut();
		pc.setExpression("execution(* com.goat.chapter400.annotation.service..*(..))");
		assertTrue(AopUtils.canApply(pc, BarService.class));
		assertTrue(AopUtils.canApply(pc, HelloServiceImpl.class));
		assertTrue(AopUtils.canApply(pc, HelloService.class));
	}

	/**
	 * 我们的目标是，在不修改 HelloService 的前提下，
	 * 为 HelloServiceImpl 额外添加 IAddition 接口，
	 * 并使用AdditionImpl作为其实现。
	 * 意思就是，当我得到 HelloServiceImpl 对象的时候，我可以强转为IAddition接口，
	 * 并执行additional方法，打印出"out additional..."；
	 * 要实现这个需求非常方便，只需要在@Aspect注解类中添加
	 *  @DeclareParents(value = "com.goat.chapter400.annotation.service.HelloService+",defaultImpl = AdditionImpl.class)
	 * 	public IAddition addition;
	 *
	 * 1，public IAddition addition字段，代表我要引入的接口；
	 * 2，@DeclareParents标签说明这个接口要引入的目标，其中value="cn.wolfcode.springboot.utilstest.IEmployeeService+"代表要引入到的目标类是IEmployeeService及其所有子类；
	 * 3，defaultImpl代表接口默认的实现，即我们的AdditionImpl类；
	 * 到这里，一个基本的introduction就已经配置完成；
	*/
	@Test
	public void test5(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		HelloService helloService = context.getBean(HelloService.class);
		((IAddition)helloService).addtional();
	}
}
