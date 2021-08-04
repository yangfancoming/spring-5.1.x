package com.goat.chapter201.bean.constructorarg;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * <constructor-arg> 标签测试用例
 * 源码搜索串：parseConstructorArgElements(ele, bd);
*/
public class App {


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
