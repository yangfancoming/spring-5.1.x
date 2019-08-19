package com.goat.chapter201.bean;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/12.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/12---20:31
 */
public class App {

	ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:beans.xml");

	/**  测试 xml 配置方式  */
	@Test
	public void test1(){
		Person person = (Person)ac.getBean("person");
		System.out.println(person);
	}
}
