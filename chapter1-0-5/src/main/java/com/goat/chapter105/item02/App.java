package com.goat.chapter105.item02;

import com.goat.chapter105.BaseTest;
import com.goat.chapter105.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: 注解方式 获取bean
 * @ author  山羊来了
 * @ date 2020/3/17---13:24
 */
public class App extends BaseTest {

	ApplicationContext ac = new AnnotationConfigApplicationContext(PersonConfig.class);

	/**
	 * ***---***personConfig
	 * ***---***pers1on
	*/
	@Test
	public void gaga(){
		look(ac);
	}


	// 注解方式 @bean  默认单例
	@Test
	public void bean(){
		System.out.println("开始 getBean");
		Person person1 = (Person) ac.getBean("person333");
		Person person2 = (Person) ac.getBean("person333");
		Assert.assertTrue(person1 == person2);
	}

	// 注解方式 @bean  原型模式
	@Test
	public void bean111(){
		System.out.println("开始 getBean");
		Person person1 = (Person) ac.getBean("person111");
		Person person2 = (Person) ac.getBean("person111");
		Assert.assertFalse(person1 == person2);
	}

	// 注解方式 @bean  默认单例 懒加载
	@Test
	public void person222(){
		System.out.println("开始 getBean");
		Person person1 = (Person) ac.getBean("person222");
		Person person2 = (Person) ac.getBean("person222");
		Assert.assertTrue(person1 == person2);
	}

	/**
	 * 注解方式 @ComponentScans
	 * ***---***	 componentScansConfig
	 * ***---***	 testController
	 * ***---***	 testDao
	 * ***---***	 testService
	 */
	@Test
	public void ComponentScans(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentScansExcludeFiltersConfig.class);
		look(ac);
	}

	@Test
	public void ComponentScans1(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentScansIncludeFiltersConfig.class);
		look(ac);
	}
}
