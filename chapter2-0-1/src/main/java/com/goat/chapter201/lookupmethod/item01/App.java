package com.goat.chapter201.lookupmethod.item01;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 引出问题：
 * 现在考虑这样一种情况，一个 singleton 类型的 bean 中有一个 prototype 类型的成员变量。
 * BeanFactory 在实例化 singleton 类型的 bean 时，会向其注入一个 prototype 类型的实例。
 * 但是 singleton 类型的 bean 只会实例化一次，那么它内部的 prototype 类型的成员变量也就不会再被改变。
 * 但如果我们每次从 singleton bean 中获取这个 prototype 成员变量时，都想获取一个新的对象。
 * 这个时候怎么办？
 * lookup-method  应运而生
 * 使用 lookup-method ，Spring 会在运行时对 NewsProvider 进行增强，使其 getNews 可以每次都返回一个新的实例
 *
 * @see com.goat.chapter201.lookupmethod.item02.App#test()   解决方式一：  xml   <lookup-method /> 标签
 * @see com.goat.chapter201.lookupmethod.item03.App#test()   解决方式二：  NewsProvider 实现 ApplicationContextAware 接口
 * @see com.goat.chapter201.lookupmethod.item04.App#test()   解决方式三：  使用 @Lookup注解
 * @see com.goat.chapter201.lookupmethod.item05.App#test()   解决方式四：  使用 proxyMode = ScopedProxyMode.TARGET_CLASS
 */
@ComponentScan("com.goat.chapter201.lookupmethod.item01")
public class App {

	/**
	 * 两次获取的 NewsProvider 和 其中的news 都是相同的，尽管news是原型模式。
	*/
	@Test
	public void test(){
		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(App.class);
		NewsProvider bean1 = ac.getBean(NewsProvider.class);
		NewsProvider bean2 = ac.getBean(NewsProvider.class);
		Assert.assertSame(bean1,bean2);
		Assert.assertSame(bean1.getNews(),bean2.getNews());
	}
}
