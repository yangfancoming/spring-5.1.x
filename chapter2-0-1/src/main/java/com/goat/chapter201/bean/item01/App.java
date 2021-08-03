package com.goat.chapter201.bean.item01;

import com.goat.chapter201.model.Person;
import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/12.
 * @ Description: 读取 bean 配置到 AbstractBeanDefinition
 * @ author  山羊来了
 * @ date 2019/8/12---20:31
 */
public class App {


	/**
	 * xml 方式将<bean>标签解析为 AbstractBeanDefinition
	 * @see BeanDefinitionParserDelegate#parseBeanDefinitionElement(org.w3c.dom.Element, java.lang.String, org.springframework.beans.factory.config.BeanDefinition)
	 * 解析为AbstractBeanDefinition后，将其注册到容器中
	 * @see BeanDefinitionReaderUtils#registerBeanDefinition(org.springframework.beans.factory.config.BeanDefinitionHolder, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 * @see DefaultListableBeanFactory#registerBeanDefinition(java.lang.String, org.springframework.beans.factory.config.BeanDefinition)
	 */
	@Test
	public void test(){
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath:beans.xml");
		ConfigurableListableBeanFactory beanFactory = ac.getBeanFactory();
		BeanDefinition person = beanFactory.getBeanDefinition("person");
		System.out.println(person);
	}

	/**
	 * 读取配置文件/注解信息到内存，概念态 ---> 内存态
	 * @see AbstractApplicationContext#obtainFreshBeanFactory()
	 * @see AbstractRefreshableApplicationContext#refreshBeanFactory()
	 * @see AbstractRefreshableApplicationContext#loadBeanDefinitions(org.springframework.beans.factory.support.DefaultListableBeanFactory)
	 * @see XmlBeanDefinitionReader#registerBeanDefinitions(org.w3c.dom.Document, org.springframework.core.io.Resource)
	 * @see DefaultBeanDefinitionDocumentReader#doRegisterBeanDefinitions(org.w3c.dom.Element)
	 * @see DefaultBeanDefinitionDocumentReader#parseBeanDefinitions(org.w3c.dom.Element, org.springframework.beans.factory.xml.BeanDefinitionParserDelegate)
	 * @see DefaultBeanDefinitionDocumentReader#processBeanDefinition(org.w3c.dom.Element, org.springframework.beans.factory.xml.BeanDefinitionParserDelegate)
	 * @see BeanDefinitionParserDelegate#parseBeanDefinitionElement(org.w3c.dom.Element, org.springframework.beans.factory.config.BeanDefinition)
	 * @see BeanDefinitionReaderUtils#registerBeanDefinition(org.springframework.beans.factory.config.BeanDefinitionHolder, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 * 内存态 ---> 纯净态
	 * @see AbstractApplicationContext#finishBeanFactoryInitialization(org.springframework.beans.factory.config.ConfigurableListableBeanFactory)
	 * @see DefaultListableBeanFactory#preInstantiateSingletons()
	 * @see AbstractBeanFactory#getBean(java.lang.String)
	 * @see AbstractBeanFactory#doGetBean(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
	 * @see AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
	 * @see AbstractAutowireCapableBeanFactory#createBeanInstance(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
	 * @see AbstractAutowireCapableBeanFactory#instantiateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition)
	 * 纯净态 ---> 成熟态
	 * @see AbstractAutowireCapableBeanFactory#populateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, org.springframework.beans.BeanWrapper)
	 * @see AbstractAutowireCapableBeanFactory#applyPropertyValues(java.lang.String, org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.BeanWrapper, org.springframework.beans.PropertyValues)
	 * @see PropertyAccessor#setPropertyValues(org.springframework.beans.PropertyValues) # AbstractAutowireCapableBeanFactory
	 * @see BeanWrapperImpl.BeanPropertyHandler#setValue(java.lang.Object)
	 * @see AbstractAutowireCapableBeanFactory#initializeBean(java.lang.String, java.lang.Object, org.springframework.beans.factory.support.RootBeanDefinition)
	 * @see AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization(java.lang.Object, java.lang.String)
	 */

	@Test
	public void test1(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:beans.xml");
		Person person = (Person)ac.getBean("person");
		System.out.println(person);
	}
}
