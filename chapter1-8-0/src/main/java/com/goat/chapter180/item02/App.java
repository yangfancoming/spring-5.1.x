package com.goat.chapter180.item02;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;


/**
 * Created by 64274 on 2019/8/17.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/17---19:18
 */
public class App {

	private XmlBeanFactory xmlBeanFactory;

	@Before
	public void initXmlBeanFactory() {
		System.setProperty("spring.profiles.active", "dev");
		System.out.println("\n========测试方法开始=======\n");
		xmlBeanFactory = new XmlBeanFactory(new ClassPathResource("bean1.xml"));
	}

	/**
	 * 默认构造器
	 * @see AbstractAutowireCapableBeanFactory#instantiateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition)
	*/
	@Test
	public void test1() {
		Dog dog1 = xmlBeanFactory.getBean("dog1", Dog.class);
		dog1.sayHello();
	}


	/**
	 * 指定构造器
	 */
	@Test
	public void test2() {
		Dog dog2 = xmlBeanFactory.getBean("dog2", Dog.class);
		dog2.sayHello();
	}

	/**
	 * 静态工厂
	 * @see AbstractAutowireCapableBeanFactory#instantiateUsingFactoryMethod(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
	 * 通过静态工厂方法的方式特殊之处在于，包含了这个静态方法的类，不需要被实例化，不需要被Spring管理，Spring的调用逻辑大概是：
	 * 1.通过<bean>标签中的class属性得到一个Class对象
	 * 2.通过Class对象获取到对应的方法名称得到Method对象
	 * 3.最后反射调用Method.invoke(null, args)
	 * 因为是静态方法，方法在执行时，不需要一个对象
	 */
	@Test
	public void test3() {
		Dog dog3 = xmlBeanFactory.getBean("dog3", Dog.class);
		dog3.sayHello();
	}

	/**
	 * 实例工厂
	 * @see AbstractAutowireCapableBeanFactory#instantiateUsingFactoryMethod(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
	 */
	@Test
	public void test4() {
		Dog dog4 = xmlBeanFactory.getBean("dog4", Dog.class);
		dog4.sayHello();
	}

	@After
	public void after() {
		System.out.println("\n========测试方法结束=======\n");
	}

}

