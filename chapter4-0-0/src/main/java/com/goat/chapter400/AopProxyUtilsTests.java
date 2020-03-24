

package com.goat.chapter400;


import com.goat.chapter400.annotation.service.BarService;
import com.goat.chapter400.annotation.service.HelloService;
import com.goat.chapter400.annotation.service.HelloServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;


@ComponentScan("com.goat.chapter400.annotation")
@EnableAspectJAutoProxy
public class AopProxyUtilsTests {

	// 获取一个代理对象的最终对象类型
	@Test
	public void testUltimateTargetClass() {
		ApplicationContext context = new AnnotationConfigApplicationContext(AopProxyUtilsTests.class);

		HelloService helloService = context.getBean(HelloService.class);
		helloService.sayHiService1("123");
		Assert.assertEquals(HelloServiceImpl.class,AopProxyUtils.ultimateTargetClass(helloService));

		BarService	barService = (BarService) context.getBean("barService");
		barService.test();
		Assert.assertEquals(BarService.class,AopProxyUtils.ultimateTargetClass(barService));
	}

	//  可以看到，不管是JDKproxy，还是cglib proxy，代理出来的对象都实现了org.springframework.aop.framework.Advised接口；
	@Test
	public void testUltimateTargetClass2() {
		ApplicationContext context = new AnnotationConfigApplicationContext(AopProxyUtilsTests.class);
		HelloService helloService = context.getBean(HelloService.class);
		BarService	barService = (BarService) context.getBean("barService");
		/**
		 * interface com.goat.chapter400.annotation.service.HelloService
		 * interface com.goat.chapter400.annotation.service.IAddition
		 * interface org.springframework.aop.SpringProxy
		 * interface org.springframework.aop.framework.Advised
		 * interface org.springframework.core.DecoratingProxy
		 * interface java.io.Serializable
		*/
		System.out.println(StringUtils.collectionToDelimitedString(ClassUtils.getAllInterfacesAsSet(helloService), "\r\n"));
		System.out.println("-----------------");
		/**
		 * interface org.springframework.aop.SpringProxy
		 * interface org.springframework.aop.framework.Advised
		 * interface org.springframework.cglib.proxy.Factory
		*/
		System.out.println(StringUtils.collectionToDelimitedString(ClassUtils.getAllInterfacesAsSet(barService), "\r\n"));
	}

	// 意思就是Advised和TargetSource接口虽然在继承关系上，都是继承了TargetClassAware接口，看似平级关系，实际上确实组合关系
	@Test
	public void testUltimateTargetClass3() {
		ApplicationContext context = new AnnotationConfigApplicationContext(AopProxyUtilsTests.class);
		HelloService helloService = context.getBean(HelloService.class);
		if (helloService instanceof Advised) {
			TargetSource ts = ((Advised) helloService).getTargetSource();
			// class org.springframework.aop.target.SingletonTargetSource
			System.out.println(ts.getClass());
		}
	}

}
