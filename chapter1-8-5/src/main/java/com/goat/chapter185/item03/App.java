package com.goat.chapter185.item03;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: 测试 BeanNameAware
 * @ author  山羊来了
 * @ date 2019/8/17---20:40
 *
 * setBeanName:16, BeanNameAwareTest (com.goat.chapter185.item03)
 * invokeAwareMethods:1804, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
 * initializeBean:1772, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
 * doCreateBean:607, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
 * createBean:489, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
 * lambda$doGetBean$10:297, AbstractBeanFactory (org.springframework.beans.factory.support)
 * getSingleton:251, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
 * doGetBean:295, AbstractBeanFactory (org.springframework.beans.factory.support)
 * getBean:1400, AbstractBeanFactory (org.springframework.beans.factory.support)
 * preInstantiateSingletons:772, DefaultListableBeanFactory (org.springframework.beans.factory.support)
 * finishBeanFactoryInitialization:800, AbstractApplicationContext (org.springframework.context.support)
 * refresh:500, AbstractApplicationContext (org.springframework.context.support)
 * <init>:126, ClassPathXmlApplicationContext (org.springframework.context.support)
 * <init>:70, ClassPathXmlApplicationContext (org.springframework.context.support)
 * <init>:15, App (com.goat.chapter185.item03)
 */
public class App {

	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean3.xml");

	@Test
	public void test() {
		System.out.println(applicationContext);
	}

	@Test
	public void test1() {
		BeanNameAwareTest temp = applicationContext.getBean("gaga", BeanNameAwareTest.class);
		System.out.println(temp);
	}

}
