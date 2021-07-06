package com.goat.chapter201.autowire.item01;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 自动注入  xml 版
*/
public class App {

	/**
	 *  测试 autowire  通过 byName 方式
	 * 从测试结果可以看出，两种方式配置方式都能完成解决 bean 之间的依赖问题。
	 * 只不过使用 autowire2.xml 会更加省力一些，配置文件也不会冗长。
	 * 这里举的例子比较简单，假使一个 bean 依赖了十几二十个 bean，再手动去配置，恐怕就很难受了。
	 */
	@Test
	public void test1(){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:autowire1.xml");
		Assert.assertTrue(context.containsBean("service-without-autowire"));
	}

	/**
	 * 将autowire="byName"配置 设置到AbstractBeanDefinition中，入口：
	 * @see BeanDefinitionParserDelegate#parseBeanDefinitionAttributes(org.w3c.dom.Element, java.lang.String, org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.factory.support.AbstractBeanDefinition)
	 * 出口
	 * @see AbstractAutowireCapableBeanFactory#populateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, org.springframework.beans.BeanWrapper)
	*/
	@Test
	public void test2(){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:autowire2.xml");
		Assert.assertTrue(context.containsBean("service-with-autowire"));
	}
}
