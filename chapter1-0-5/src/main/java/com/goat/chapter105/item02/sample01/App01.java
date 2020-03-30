package com.goat.chapter105.item02.sample01;

import com.goat.chapter105.BaseTest;
import com.goat.chapter105.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description:  学习@bean 注解
 * @ author  山羊来了
 * @ date 2020/3/17---13:24
 */
public class App01 extends BaseTest {

	@Test
	public void test1(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(Bean01Config.class);
		Person person = (Person) ac.getBean("person");
		System.out.println(person);
	}

	// 测试 多个配置文件中含有相同beanId的，后面的配置文件会覆盖前面的bean定义
	@Test
	public void test2(){
		Class<?> [] classes1 = { Bean01Config.class,Bean02Config.class};
		ApplicationContext ac = new AnnotationConfigApplicationContext(classes1);
		Person person = (Person) ac.getBean("person");
		Assert.assertEquals("app02",person.getName());

		Class<?> [] classes2 = { Bean02Config.class,Bean01Config.class};
		ApplicationContext ac2 = new AnnotationConfigApplicationContext(classes2);
		Person person2 = (Person) ac2.getBean("person");
		Assert.assertEquals("app01",person2.getName());
	}
}
