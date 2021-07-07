package com.goat.chapter201.bean.lazy.item02;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2021/7/1.
 * @ Description: 注解版示例
 * @ author  山羊来了
 * @ date 2021/7/1---19:23
 */
public class App {


	/**
	 * A 和 B 都是正常bean的加载情况
	*/
	@Test
	public void test1(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config1.class);
		ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
		Assert.assertTrue(beanFactory.containsSingleton("a"));
		Assert.assertTrue(beanFactory.containsSingleton("b"));
	}

	/**
	 * A 是正常bean  B 是懒加载bean 的加载情况
	 */
	@Test
	public void test2(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config2.class);
		ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
		Assert.assertTrue(beanFactory.containsSingleton("a"));
		Assert.assertFalse(beanFactory.containsSingleton("b"));
	}
}
