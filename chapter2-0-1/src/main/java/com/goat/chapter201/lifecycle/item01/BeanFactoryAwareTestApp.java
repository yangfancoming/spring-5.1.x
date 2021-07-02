package com.goat.chapter201.lifecycle.item01;

import com.goat.chapter201.model.Dog;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class BeanFactoryAwareTestApp {

	/**  测试 BeanFactoryAware  从自定义的容器中 获取 bean  */
	@Test
	public void test1() {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("BeanFactoryAwareTest.xml");
		BeanFactoryAwareTest beanFactoryAwareTest = (BeanFactoryAwareTest) ac.getBean("gaga2");
		BeanFactory factory = beanFactoryAwareTest.getFactory();
		// 通过自定义的bean  (MyBeanFactoryAware)获取 IOC容器中的任意其他bean对象
		Dog dog = (Dog) factory.getBean("dog");
		dog.sayHello();
	}

}
