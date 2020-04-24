package com.goat.chapter201.dependson;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class App {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:depends-on.xml");

	/**
	 * 当一个 bean 直接依赖另一个 bean，可以使用 <ref/> 标签进行配置。
	 * 不过如某个 bean 并不直接依赖于其他 bean，但又需要其他 bean 先实例化好，这个时候就需要使用 depends-on 特性了
	 * 从测试结果可以看出 虽然获取的是 hello bean  但是 world 的bean 构造函数 先执行！
	 */
	@Test
	public void test31(){
		System.out.println("hello -> " + context.getBean("hello"));
	}
}
