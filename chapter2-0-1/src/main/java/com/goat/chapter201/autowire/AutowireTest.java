package com.goat.chapter201.autowire;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/16---11:26
 */
public class AutowireTest {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:autowire.xml");

	/**
	 *  测试 autowire  通过 byName 方式
	 *
	 * 从测试结果可以看出，两种方式配置方式都能完成解决 bean 之间的依赖问题。
	 * 只不过使用 autowire 会更加省力一些，配置文件也不会冗长。
	 * 这里举的例子比较简单，假使一个 bean 依赖了十几二十个 bean，再手动去配置，恐怕就很难受了。
	 */
	@Test
	public void test31(){
		System.out.println("service-without-autowire -> " + context.getBean("service-without-autowire"));
		System.out.println("service-with-autowire -> " + context.getBean("service-with-autowire"));
	}
}
