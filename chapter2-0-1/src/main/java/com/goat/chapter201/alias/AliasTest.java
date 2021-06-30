package com.goat.chapter201.alias;

import com.goat.chapter201.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @see DefaultBeanDefinitionDocumentReader#processAliasRegistration(org.w3c.dom.Element)
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
		Assert.assertEquals("jordan",person3.getName());
		Assert.assertEquals(23,person3.getAge());
	}

}
