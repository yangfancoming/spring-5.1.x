package com.goat.chapter201.bean.constructorarg;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * <constructor-arg> 标签 使用type
 * 源码搜索串：parseConstructorArgElements(ele, bd);
*/
public class AppType {


	// 测试 双参构造函数
	@Test
	public void test1(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:constructorarg21.xml");
		TestConstructorArg constructorArg = (TestConstructorArg) ac.getBean("test");
		Assert.assertEquals("Goat",constructorArg.getName());
		Assert.assertEquals(23,constructorArg.getAge());
	}

	// 测试 使用单参 age 构造函数
	@Test
	public void test2(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:constructorarg22.xml");
		TestConstructorArg constructorArg = (TestConstructorArg) ac.getBean("test");
		// 未赋值，使用默认值
		Assert.assertEquals(null,constructorArg.getName());
		Assert.assertEquals(23,constructorArg.getAge());
	}

	// 测试
	@Test
	public void test3(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:constructorarg23.xml");
		TestConstructorArg constructorArg = (TestConstructorArg) ac.getBean("test");
		System.out.println(constructorArg);
	}
}
