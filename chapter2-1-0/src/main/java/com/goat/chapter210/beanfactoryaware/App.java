package com.goat.chapter210.beanfactoryaware;

import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/10---11:28
 */
@ComponentScan("com.goat.chapter210")
public class App {

	/**  测试 BeanPostProcessor   */
	@Test
	public void test1(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		System.out.println(context);
	}

	/**  测试 BeanFactoryAware   */
	@Test
	public void test2(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		MyBeanFactoryAware simpleHello = (MyBeanFactoryAware) context.getBean("myBeanFactoryAware");
		BeanFactory factory = simpleHello.getFactory();
		 //通过自定义的bean获取 IOC容器的任意对象
		TestBean testBean = (TestBean) factory.getBean("testBean");
		testBean.test();
	}
}
