package com.goat.chapter201;

import com.goat.chapter201.bean.Person;
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

	ApplicationContext ac1 = new ClassPathXmlApplicationContext("classpath:beans.xml");

	/**  测试 xml 配置方式  */
	@Test
	public void test1(){
		Person person = (Person)ac1.getBean("person");
		System.out.println(person);
	}
}