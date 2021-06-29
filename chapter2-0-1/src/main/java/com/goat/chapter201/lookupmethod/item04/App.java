package com.goat.chapter201.lookupmethod.item04;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;


/**
 * 解决方式三：  NewsProvider 通过使用 @Lookup 注解的方式
*/
@ComponentScan("com.goat.chapter201.lookupmethod.item04")
public class App {

	/**
	 * @Lookup 使用方式一
	 * 两次获取的 NewsProvider 和 其中的news 都是不同的实例
	*/
	@Test
	public void test(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(App.class);
		NewsProvider bean1 = ac.getBean(NewsProvider.class);
		NewsProvider bean2 = ac.getBean(NewsProvider.class);
		Assert.assertSame(bean1,bean2);
		Assert.assertNotSame(bean1.getNews(),bean2.getNews());

		bean1.setName("1");
		System.out.println(bean1.getName());
	}


	// @Lookup 使用方式二
	@Test
	public void test2(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(App.class);
		NewsProvider2 bean1 = ac.getBean(NewsProvider2.class);
		NewsProvider2 bean2 = ac.getBean(NewsProvider2.class);
		Assert.assertSame(bean1,bean2);
		Assert.assertNotSame(bean1.getNews(),bean2.getNews());
	}
}
