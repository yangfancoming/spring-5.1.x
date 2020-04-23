package com.goat.chapter201.replacedmethod;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 源码搜索串： parseReplacedMethodSubElements(ele, bd.getMethodOverrides());
 *
 * <replaced-method> 方法的用途：可以在运行时用新的方法替换现有的方法。不仅可以动态地替换返回实体 bean，还能动态地更改原有方法的逻辑。
 * 简单来说，就是将某个类定义的方法，在运行时替换成另一个方法，例如明明看到代码中调用的是 A 方法，但实际运行的却是 B 方法。
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
