package com.goat.chapter201.cyclicdependency.item02;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/4/3.
 *
 * @ Description: 构造函数的循环依赖，无法解决原理
 * @ author  山羊来了
 * @ date 2020/4/3---15:15
 *
 * 依赖注入分为三种：
 * 1. set 方法注入
 * 2. 构造方法注入
 * 3. 工厂方法注入
 *
 * 由于2.构造方法注入，是将对象的实例化和初始化两步，合二为一了，所以无法解决。
 * 由于1.set 方法注入，是将对象的实例化和初始化分开的两个步骤，因此可以解决。
 * 提前暴露对象，该对象指的是 已经完成了实例化，但是并未完成初始化的，将其添加到二级缓冲池，进行提前暴露。
 */
public class App {

	/**
	 * 循环依赖导致报错：
	 * Caused by: org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'a': Requested bean is currently in creation: Is there an unresolvable circular reference?
	 * 	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.beforeSingletonCreation(DefaultSingletonBeanRegistry.java:339)
	 * 	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:215)
	 * 	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:318)
	 * 	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199)
	 *
	 * 	构造器注入构成的循环依赖，此种循环依赖方式是无法解决的，只能抛出BeanCurrentlyInCreationException异常表示循环依赖。
	 * 	这也是构造器注入的最大劣势（它有很多独特的优势，请小伙伴自行发掘）
	 *  根本原因：Spring解决循环依赖依靠的是Bean的“纯净态”这个概念，而这个中间态指的是已经实例化，但还没初始化的状态。
	 *  而构造器是完成实例化的东东，所以构造器的循环依赖无法解决~~~
	*/
	@Test
	public void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		System.out.println(ac);
	}
}
