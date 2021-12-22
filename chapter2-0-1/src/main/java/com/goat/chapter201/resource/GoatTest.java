package com.goat.chapter201.resource;

import com.goat.chapter201.model.Person;
import org.junit.Test;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * @see DefaultBeanDefinitionDocumentReader#processAliasRegistration(org.w3c.dom.Element)
 */
public class GoatTest {

	/**
	 * PropertyPlaceholderConfigurer
	 *
	 * 功能 : 解析和处理bean定义中属性值,构造函数参数值,和@Value注解中的占位符${...}
	 * 属性源 : 所设置的Properties属性对象,属性文件,系统属性(system properties)，环境变量(environment variables)
	 * 工作模式 : “拉”，遍历每个bean定义中的属性占位符，从属性源中拉取对应的属性值替换属性占位符
	 * 从Spring 3.1 开始，推荐使用PropertySourcesPlaceholderConfigurer而不是PropertyPlaceholderConfigurer。
	 */
	@Test
	public void test1(){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:resource1.xml");
		GoatDataSource dataSource = context.getBean("dataSource", GoatDataSource.class);
		System.out.println(dataSource);
	}


	/**
	 * PropertyOverrideConfigurer
	 *
	 * 功能 : 基于属性文件定义的bean属性值设置指令执行相应的bean属性值设置
	 * 属性源 : 指定路径的属性文件
	 * 工作模式: “推”,根据属性文件中的bean属性设置指令将属性值推送设置到相应的bean属性
	 * e.getAge() // 这里会对应属性文件 myproperties.properties 中的属性值 40
	 * e.getName() // => 这里会对应属性文件 myproperties.properties 中的属性值 "Stanis"
	*/
	@Test
	public void test2(){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:resource2.xml");
		Person person = context.getBean("person", Person.class);
		System.out.println(person);
	}

}
