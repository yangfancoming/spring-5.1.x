package com.goat.chapter180.item03;

import org.junit.Test;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2021/6/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/16---14:52
 */
public class App {

	/**
	 *
	 * @see AbstractAutowireCapableBeanFactory#instantiateUsingFactoryMethod(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
	*/
	@Test
	public void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		MyService bean = ac.getBean(MyService.class);
		System.out.println(bean);
	}
}
