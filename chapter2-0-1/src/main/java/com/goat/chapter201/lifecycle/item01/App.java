package com.goat.chapter201.lifecycle.item01;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: 测试 BeanNameAware
 * @ author  山羊来了
 * @ date 2019/8/17---20:40
 */
public class App {

	@Test
	public void test1() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("BeanNameAwareTest.xml");
		BeanNameAwareTest temp = applicationContext.getBean("gaga", BeanNameAwareTest.class);
		System.out.println(temp);
	}
}
