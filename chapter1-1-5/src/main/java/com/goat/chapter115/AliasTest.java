package com.goat.chapter115;

import com.goat.chapter115.bean.Person;
import org.junit.Assert;
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
public class AliasTest extends BaseApp {

	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:alias.xml");

	/**
	 * 通过 别名 <alias>标签 获取bean测试 测试
	 */
	@Test
	public void test31(){
		Person person1 = (Person) context.getBean("person");
		Person person2 = (Person) context.getBean("goat");
		Person person3 = (Person) context.getBean("goatLike");
		// 验证 获取的都是同一个bean
		Assert.assertTrue(person1 == person2 );
		Assert.assertTrue(person2 == person3 );
		System.out.println(person1.toString());
	}
}
