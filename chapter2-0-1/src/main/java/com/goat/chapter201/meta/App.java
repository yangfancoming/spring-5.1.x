package com.goat.chapter201.meta;

import com.goat.chapter201.common.Person;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * 源码搜索串：  parseMetaElements(ele, bd)
 * parseBeanDefinitionElement:428, BeanDefinitionParserDelegate (org.springframework.beans.factory.xml)
 * parseBeanDefinitionElement:340, BeanDefinitionParserDelegate (org.springframework.beans.factory.xml)
 * parseBeanDefinitionElement:299, BeanDefinitionParserDelegate (org.springframework.beans.factory.xml)
 * processBeanDefinition:310, DefaultBeanDefinitionDocumentReader (org.springframework.beans.factory.xml)
 * parseDefaultElement:207, DefaultBeanDefinitionDocumentReader (org.springframework.beans.factory.xml)
 * parseBeanDefinitions:179, DefaultBeanDefinitionDocumentReader (org.springframework.beans.factory.xml)
 * doRegisterBeanDefinitions:144, DefaultBeanDefinitionDocumentReader (org.springframework.beans.factory.xml)
 * registerBeanDefinitions:80, DefaultBeanDefinitionDocumentReader (org.springframework.beans.factory.xml)
 * registerBeanDefinitions:436, XmlBeanDefinitionReader (org.springframework.beans.factory.xml)
 * doLoadBeanDefinitions:342, XmlBeanDefinitionReader (org.springframework.beans.factory.xml)
 * loadBeanDefinitions:292, XmlBeanDefinitionReader (org.springframework.beans.factory.xml)
 * loadBeanDefinitions:489, XmlBeanDefinitionReader (org.springframework.beans.factory.xml)
 * loadBeanDefinitions:199, AbstractBeanDefinitionReader (org.springframework.beans.factory.support)
 * loadBeanDefinitions:145, AbstractBeanDefinitionReader (org.springframework.beans.factory.support)
 * loadBeanDefinitions:206, AbstractBeanDefinitionReader (org.springframework.beans.factory.support)
 * loadBeanDefinitions:214, AbstractBeanDefinitionReader (org.springframework.beans.factory.support)
 * loadBeanDefinitions:109, AbstractXmlApplicationContext (org.springframework.context.support)
 * loadBeanDefinitions:73, AbstractXmlApplicationContext (org.springframework.context.support)
 * refreshBeanFactory:107, AbstractRefreshableApplicationContext (org.springframework.context.support)
 * obtainFreshBeanFactory:582, AbstractApplicationContext (org.springframework.context.support)
 * refresh:448, AbstractApplicationContext (org.springframework.context.support)
 * <init>:126, ClassPathXmlApplicationContext (org.springframework.context.support)
 * <init>:70, ClassPathXmlApplicationContext (org.springframework.context.support)
 * <init>:11, App (com.goat.chapter201.meta)
*/
public class App {

	ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:meta.xml");

	// <meta> 标签测试用例
	@Test
	public void test1(){
		Person person = (Person)ac.getBean("person");
		System.out.println(person);
	}
}
