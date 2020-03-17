package com.goat.chapter105.item02;

import com.goat.chapter105.BaseTest;
import com.goat.chapter105.model.Person;
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


	// 注解方式 @bean
	@Test
	public void bean(){
		Person person = (Person) ac.getBean("pers1on");
		System.out.println(person);
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
		ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentScansConfig.class);
		look(ac);
	}
}
