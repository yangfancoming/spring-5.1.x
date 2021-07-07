package com.goat.chapter201.bean.lazy.item01;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2021/7/1.
 * @ Description: xml 版 懒加载示例
 * @ author  山羊来了
 * @ date 2021/7/1---19:23
 */
public class App {


	/**  懒加载bean 只有在第一次使用时 才实例化  测试
	 * @see DefaultListableBeanFactory#preInstantiateSingletons()
	 * @see AbstractBeanDefinition#isLazyInit()
	 */
	@Test
	public void test(){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:lazy.xml");
		ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
		// 容器初始化时 并没有创建懒加载的bean
		Assert.assertFalse(beanFactory.containsSingleton("lazyService"));
		context.getBean("lazyService");
		// 第一次getBean后 才创建懒加载的bean
		Assert.assertTrue(beanFactory.containsSingleton("lazyService"));
	}
}
