package com.goat.chapter201.bean.constructorarg;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * <constructor-arg> 标签  使用index下标
 * 源码搜索串：parseConstructorArgElements(ele, bd);
 * 缺点是 如果有2个单参构造函数，使用index=0，则只能使用其中一个，若想使用另一个，那么需要借助type
*/
public class AppIndex {


	// 测试 双参构造函数
	@Test
	public void test1(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:constructorarg1.xml");
		TestConstructorArg constructorArg = (TestConstructorArg) ac.getBean("testConstructorArg");
		Assert.assertEquals("Goat",constructorArg.getName());
		Assert.assertEquals(23,constructorArg.getAge());
	}

	// 测试 使用单参 name 构造函数
	@Test
	public void test2(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:constructorarg2.xml");
		TestConstructorArg constructorArg = (TestConstructorArg) ac.getBean("test");
		Assert.assertEquals("Goat",constructorArg.getName());
		// 未赋值，使用默认值
		Assert.assertEquals(0,constructorArg.getAge());
	}

	// 测试 使用单参 age 构造函数   需要指定参数类型
	@Test
	public void test3(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:constructorarg3.xml");
		TestConstructorArg constructorArg = (TestConstructorArg) ac.getBean("test");
		// 未赋值，使用默认值
		Assert.assertEquals(null,constructorArg.getName());
		Assert.assertEquals(23,constructorArg.getAge());
	}
}
