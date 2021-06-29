package com.goat.chapter201.lookupmethod.item03;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;



/**
 * 解决方式二：  NewsProvider 实现 ApplicationContextAware 拿到bean工厂，每次都getBean获取不同news实例。
*/
@ComponentScan("com.goat.chapter201.lookupmethod.item03")
public class App {

	/**
	 * 两次获取的 NewsProvider 和 其中的news 都是不同的实例
	*/
	@Test
	public void test(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(App.class);
		NewsProvider bean1 = ac.getBean(NewsProvider.class);
		NewsProvider bean2 = ac.getBean(NewsProvider.class);
		Assert.assertSame(bean1,bean2);
		Assert.assertNotSame(bean1.getNews(),bean2.getNews());
	}
}
