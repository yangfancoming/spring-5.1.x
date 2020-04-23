package com.goat.chapter201.replacedmethod;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 源码搜索串： parseReplacedMethodSubElements(ele, bd.getMethodOverrides());
*/
public class App {

	ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:replacedmethod.xml");

	// <replaced-method> 标签测试用例
	@Test
	public void test1(){
		// replace 测试
		BeforeMethodReplaced methodReplaced = (BeforeMethodReplaced) ac.getBean("beforeMethodReplaced");
		// 输出 change it, i am a fake book
		methodReplaced.printDefaultName();
	}
}
