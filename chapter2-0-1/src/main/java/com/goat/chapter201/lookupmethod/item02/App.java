package com.goat.chapter201.lookupmethod.item02;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



/**
 * 解决方式一：  xml   <lookup-method /> 标签
*/
public class App {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:lookup-method.xml");

	/**
	 *
	 * 从上面的结果可以看出，new1 和 new2 指向了不同的对象。
	 * 同时，注意看 newsProvider，似乎变的很复杂。
	 * 由此可看出，NewsProvider 被 CGLIB 增强了。
	 */
	@Test
	public void test(){
		NewsProvider newsProvider = (NewsProvider) context.getBean("newsProvider");
		Assert.assertNotSame(newsProvider.getNews(),newsProvider.getNews());
	}
}
