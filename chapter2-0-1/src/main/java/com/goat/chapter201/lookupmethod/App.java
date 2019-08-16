package com.goat.chapter201.lookupmethod;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * lookup-method  应运而生
 * 现在考虑这样一种情况，一个 singleton 类型的 bean 中有一个 prototype 类型的成员变量。
 * BeanFactory 在实例化 singleton 类型的 bean 时，会向其注入一个 prototype 类型的实例。
 * 但是 singleton 类型的 bean 只会实例化一次，那么它内部的 prototype 类型的成员变量也就不会再被改变。
 * 但如果我们每次从 singleton bean 中获取这个 prototype 成员变量时，都想获取一个新的对象。
 * 这个时候怎么办？
 * 使用 lookup-method ，Spring 会在运行时对 NewsProvider 进行增强，使其 getNews 可以每次都返回一个新的实例
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
	public void test31(){
		NewsProvider newsProvider = (NewsProvider) context.getBean("newsProvider");
		System.out.println(newsProvider.getNews());
		System.out.println(newsProvider.getNews());
	}
}
