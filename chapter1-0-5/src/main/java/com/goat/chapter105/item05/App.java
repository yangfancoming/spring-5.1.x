package com.goat.chapter105.item05;

import com.goat.chapter105.BaseTest;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 使用 Spring 提供的 FactoryBean 来向容器中注册bean
*/
public class App extends BaseTest {

	ApplicationContext ac = new AnnotationConfigApplicationContext(FactoryBeanConfig.class);

	@Test
	public void getBean(){
		look(ac);
		// 这里获取的是 ColorFactoryBean调用 getObject 方法返回的bean  （com.goat.chapter105.model.Red）
		Object test = ac.getBean("test");
		// com.goat.chapter105.model.Red
		System.out.println(test.getClass());
	}

	@Test
	public void ImportConfig(){
		look(ac);
		// 这里加上 & 前缀后 可以获取到 FactoryBean 本尊
		Object test = ac.getBean("&test");
		//  com.goat.chapter105.item05.ColorFactoryBean
		System.out.println(test.getClass());
	}

}
