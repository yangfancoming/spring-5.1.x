package com.goat.chapter201.alias;

import com.goat.chapter201.common.Person;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 源码搜索串： processAliasRegistration(ele);
 */
public class AliasTest  {

	/**
	 * 通过 别名 <alias> 标签 获取bean测试
	 */
	@Test
	public void test1(){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:alias.xml");
		Person person1 = (Person) context.getBean("person");
		Person person2 = (Person) context.getBean("goat");
		Person person3 = (Person) context.getBean("goatLike");
		// 验证 通过不同的别名 获取的都是同一个bean
		Assert.assertTrue(person1 == person2 );
		Assert.assertTrue(person2 == person3 );
		System.out.println(person1.toString());
	}

}
