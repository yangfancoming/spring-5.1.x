package com.goat.chapter201.constructorarg;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 源码搜索串：parseConstructorArgElements(ele, bd);
 * <constructor-arg>
*/
public class App {

	ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:constructorarg.xml");

	// <replaced-method> 标签测试用例
	@Test
	public void test1(){
		// constructor-arg 测试
		TestConstructorArg constructorArg = (TestConstructorArg) ac.getBean("testConstructorArg");
		// 输出 TestConstructorArg{name='JingQ', age=23}
		System.out.println(constructorArg.toString());
	}
}
