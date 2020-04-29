package com.goat.chapter185.item03;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * InitializingBeanTest 调用堆栈：
 *
 * afterPropertiesSet:16, InitializingBeanTest (com.goat.chapter185.item03)
 * invokeInitMethods:1850, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
 * initializeBean:1789, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
 * doCreateBean:608, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
 * createBean:490, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
 * lambda$doGetBean$0:286, AbstractBeanFactory (org.springframework.beans.factory.support)
 * getSingleton:249, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
 * doGetBean:284, AbstractBeanFactory (org.springframework.beans.factory.support)
 * getBean:1355, AbstractBeanFactory (org.springframework.beans.factory.support)
 * preInstantiateSingletons:725, DefaultListableBeanFactory (org.springframework.beans.factory.support)
 * finishBeanFactoryInitialization:800, AbstractApplicationContext (org.springframework.context.support)
 * refresh:504, AbstractApplicationContext (org.springframework.context.support)
 * <init>:126, ClassPathXmlApplicationContext (org.springframework.context.support)
 * <init>:70, ClassPathXmlApplicationContext (org.springframework.context.support)
 * test2:46, App (com.goat.chapter185.item03)
 *  *  我们知道，
 *  *  1.实现了FactoryBean的bean会调用它的getObject方法创建bean，
 *  *  2.实现了InitializingBean的bean会在属性填充完成之后调用它的afterPropertiesSet方法
 */
public class DisposableBeanTestApp {

	@Test
	public void test2() {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("DisposableBeanTest.xml");
		System.out.println(ac);
		ac.close();
	}
}
