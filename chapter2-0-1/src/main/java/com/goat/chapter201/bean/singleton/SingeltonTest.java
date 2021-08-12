package com.goat.chapter201.bean.singleton;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SingeltonTest {



	/**
	 * 测试 <bean> 标签中 id 和name属性重复问题
	 *  异常： lineNumber: 9; columnNumber: 73; 类型为 ID 的属性值 "testId" 在文档内必须是唯一的。
	 */
	@Test
	public void test1(){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:singelton1.xml");
		Assert.assertNotNull(context);
	}

	/**  测试 getBean 的参数  既可以是 <bean> 标签的id属性 也可以是name属性 */
	@Test
	public void test2(){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:singelton2.xml");
		CompactDisc foo = (CompactDisc)context.getBean("foo");
		CompactDisc gaga = (CompactDisc)context.getBean("gaga");
		foo.play();
		gaga.play();
	}

	/**
	 * 测试 获取到的bean相等，bar 和 haha 指向同一个bean
	 * com.goat.chapter200.item01.CompactDiscImpl@2280cdac
	 * com.goat.chapter200.item01.CompactDiscImpl@2280cdac
	 */
	@Test
	public void test3(){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:singelton3.xml");
		CompactDisc bar = (CompactDisc)context.getBean("bar");
		CompactDisc haha = (CompactDisc)context.getBean("haha");
		System.out.println(bar);
		System.out.println(haha);
		Assert.assertTrue(bar == haha);
	}
}
