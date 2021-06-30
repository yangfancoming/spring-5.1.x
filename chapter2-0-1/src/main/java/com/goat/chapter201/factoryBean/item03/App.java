package com.goat.chapter201.factoryBean.item03;


import com.goat.chapter201.model.Red;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 使用 Spring 提供的 FactoryBean 来向容器中注册bean
 *
 * 应用场景
 * 1、FactoryBean 通常是用来创建比较复杂的bean，一般的bean 直接用xml配置即可，
 * 		但如果一个bean的创建过程中涉及到很多其他的bean 和复杂的逻辑，用xml配置比较困难，
 * 		这时可以考虑用FactoryBean，用户可以通过实现该接口定制实例化Bean的逻辑。
 *
 * 2、由于第三方库不能直接注册到spring容器，于是可以实现org.springframework.bean.factory.FactoryBean接口，
 * 		然后给出自己对象的实例化代码即可。 （mybatis-spring 就是这么做的）
 *
 * FactoryBean最为典型的一个应用就是用来创建AOP的代理对象
 * @see  AbstractBeanFactory#isFactoryBean(String)
 * @see  AbstractBeanFactory#isFactoryBean(String, org.springframework.beans.factory.support.RootBeanDefinition)
 * @see  AbstractBeanFactory#predictBeanType(String, org.springframework.beans.factory.support.RootBeanDefinition, Class[])
*/
public class App {

	ApplicationContext ac = new AnnotationConfigApplicationContext(FactoryBeanConfig.class);
//	look(ac);

	@Test
	public void test(){
		Object test = ac.getBean("testBean");
		System.out.println(test);
	}

	@Test
	public void test1(){
		// 这里获取的是 ColorFactoryBean 调用 getObject 方法返回的bean  （com.goat.chapter105.model.Red）
		Object test = ac.getBean("test");
		Assert.assertTrue(test instanceof Red);
	}

	@Test
	public void test2(){
		// 这里加上 & 前缀后 可以获取到 FactoryBean 本尊
		Object test = ac.getBean("&test");
		Assert.assertTrue(test instanceof ColorFactoryBean );
	}
}
