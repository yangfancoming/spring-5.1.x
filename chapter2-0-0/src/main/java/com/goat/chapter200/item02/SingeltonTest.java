package com.goat.chapter200.item02;


import com.goat.chapter200.base.CompactDisc;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SingeltonTest {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:Singelton.xml");

	/**  测试 <bean> 标签中 id 和name属性重复问题 */
	@Test
	public void test1(){
		Assert.assertNotNull(context);
	}

	/**  测试 getBean 的参数  既可以是 <bean> 标签的id属性 也可以是name属性 */
	@Test
	public void test2(){
		CompactDisc bar = (CompactDisc)context.getBean("bar");
		CompactDisc haha = (CompactDisc)context.getBean("haha");
		bar.play();
		haha.play();
	}

	/**  测试 获取到的bean相等，bar 和 haha 指向同一个bean
	 * com.goat.chapter200.item01.CompactDiscImpl@2280cdac
	 * com.goat.chapter200.item01.CompactDiscImpl@2280cdac
	 * */
	@Test
	public void test3(){
		CompactDisc bar = (CompactDisc)context.getBean("bar");
		CompactDisc haha = (CompactDisc)context.getBean("haha");
		System.out.println(bar);
		System.out.println(haha);
		Assert.assertTrue(bar == haha);
	}
}
