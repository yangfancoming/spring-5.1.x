package com.goat.chapter105.item01;

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
public class App {

	@Test
	public void test(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("person.xml");
		Person person = (Person)ac.getBean("person");
		System.out.println(person);
	}
}
