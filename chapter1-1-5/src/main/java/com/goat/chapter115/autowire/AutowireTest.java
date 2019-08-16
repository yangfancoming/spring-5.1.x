package com.goat.chapter115.autowire;

import com.goat.chapter115.BaseApp;
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
public class AutowireTest extends BaseApp {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:autowire.xml");

	/**
	 * 通过 别名 <alias>标签 获取bean测试 测试
	 */
	@Test
	public void test31(){
		System.out.println("service-without-autowire -> " + context.getBean("service-without-autowire"));
		System.out.println("service-with-autowire -> " + context.getBean("service-with-autowire"));
	}
}
