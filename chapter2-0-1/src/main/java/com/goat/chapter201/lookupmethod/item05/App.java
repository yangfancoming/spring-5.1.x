package com.goat.chapter201.lookupmethod.item05;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;


/**
 * 解决方式四： 简单来说就是如果一个bean A对另外一个作用域更短的bean B有依赖，那么在实例化bean A并注入依赖时，注入的不是bean B本身，而是一个AOP代理，这个代理可以找到实际的bean。
 * 使用这种方法的好处是仅需对bean B进行简单的配置，并且bean A根本不用意识到代理的存在，将bean B当做一个正常的bean来装载就好。
*/
@ComponentScan("com.goat.chapter201.lookupmethod.item05")
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
		Assert.assertNotSame(bean1.getNews(),bean2.getNews()); // doit ？？？ 为啥不灵？？？
	}
}
