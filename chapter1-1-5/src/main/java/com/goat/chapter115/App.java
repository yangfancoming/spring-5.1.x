package com.goat.chapter115;


import com.goat.chapter115.bean.Person;
import com.goat.chapter115.service.LazyService;
import com.goat.chapter115.service.WhatService;
import org.junit.Assert;
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
public class App {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:application.xml");

	/** 接口 测试 */
	@Test
	public void test1(){
		WhatService whatService = context.getBean(WhatService.class);
		System.out.println(whatService.getMessage());
	}
	/**
	 * 通过 name 获取bean测试 测试
	 * 通过 别名 获取bean测试 测试
	 * */
	@Test
	public void test31(){
		WhatService whatService1 = (WhatService) context.getBean("whatService");
		WhatService whatService2 = (WhatService) context.getBean("goat");
		// 验证 获取的都是同一个bean
		Assert.assertTrue(whatService1 == whatService2);
		System.out.println(whatService1.getMessage());
	}

	/**
	 * 正常bean实例化 测试  通过构造函数
	 * 懒加载bean  测试  去掉 bean标签中的 lazy-init="true"属性 再运行本测试
	 *  可以看到 xml中设置了
	 *  <bean id="lazyService" class="com.goat.chapter115.service.LazyServiceImpl" lazy-init="true"/>
	 *  MyBeanFactoryPostProcessor 中设置了 azyService.setLazyInit(false);
	 *  运行结果 是以 后者为准
	 * */
	@Test
	public void test(){}


	/**  懒加载bean 只有在第一次使用时 才实例化  测试 */
	@Test
	public void test23(){
		LazyService whatService = (LazyService) context.getBean("lazyService");
		System.out.println(whatService.getMessage());
	}


	/** 抽象bean <bean abstract="true"/>  测试   会抛出异常 DefaultListableBeanFactory#805*/
	@Test
	public void test2(){
		Object abstractBeanTest = context.getBean("abstractBeanTest");
		System.out.println(abstractBeanTest);
	}

	/** 接口 BeanFactoryPostProcessor 动态修改bean工厂中的属性  修改图纸 修改内存态 */
	@Test
	public void test32(){
		Person person = (Person) context.getBean("person");
		System.out.println(person);
	}
}
