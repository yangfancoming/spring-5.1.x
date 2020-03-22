package com.goat.chapter105.item01;

import com.goat.chapter105.BaseTest;
import com.goat.chapter105.model.Person;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: 传统xml式 获取bean
 * @ author  山羊来了
 * @ date 2020/3/17---13:24
 */
public class App extends BaseTest {

	// 传统标签 <bean>
	@Test
	public void test(){
//		ApplicationContext ac = new ClassPathXmlApplicationContext("person.xml");
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:person.xml");
		Person person = (Person)ac.getBean("person");
		System.out.println(person);
	}

	/**
	 * 传统标签 <component-scan> 可以扫描到 com.goat.chapter105.common 包下的
	 * ***---***	 testController
	 * ***---***	 testDao
	 * ***---***	 testService
	*/
	@Test
	public void test1(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("component-scan.xml");
		look(ac);
	}

}
