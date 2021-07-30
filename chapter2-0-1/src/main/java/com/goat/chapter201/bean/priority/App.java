package com.goat.chapter201.bean.priority;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2021/7/1.
 * @ Description: 学习 @Priority注解
 * @ author  山羊来了
 * @ date 2021/7/1---19:23
 */
public class App {

	/**
	 * 测试  容器中有2个名称不同，但是bd类型相同的bean，在按类型获取时会触发NoUniqueBeanDefinitionException异常
	 * @see DefaultListableBeanFactory#getBean(Class)
	 * @see DefaultListableBeanFactory#determinePrimaryCandidate(java.util.Map, Class)
	 * @see DefaultListableBeanFactory#determineHighestPriorityCandidate(java.util.Map, Class)
	 * 经过 determinePrimaryCandidate 和 determineHighestPriorityCandidate  都未能从多个候选者中筛选出优先bean的话，则抛出异常。
	 * No qualifying bean of type 'com.goat.chapter201.bean.primary.A' available: 【expected single matching bean but found 2: a1,a2】
	 */

	@Test(expected = NoUniqueBeanDefinitionException.class)
	public void test1(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config1.class);
		context.getBean(UserService.class);
	}

	@Test
	public void test2(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config2.class);
		UserService bean = context.getBean(UserService.class);
		Assert.assertTrue(bean instanceof Config2.UserServiceImpl1);
	}

	@Test(expected = NoUniqueBeanDefinitionException.class)
	public void test3(){
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config3.class);
		context.getBean(UserService.class);
	}
}
