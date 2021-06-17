package com.goat.chapter201.componentscan;

import org.junit.Test;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

/**
 * Created by Administrator on 2020/4/24.
 * @ Description: <context:component-scan> 标签解析
 * @ author  山羊来了
 * @ date 2020/4/24---14:28
 */
public class App {

	/**
	 * 传统标签 <component-scan> 可以扫描到 com.goat.chapter105.common 包下的
	 * 其中5个类中 2个没有注解 因此只有3个会被加载到spring的容器中
	 * @see DefaultBeanDefinitionDocumentReader#parseBeanDefinitions(org.w3c.dom.Element, org.springframework.beans.factory.xml.BeanDefinitionParserDelegate)
	 * @see BeanDefinitionParserDelegate#parseCustomElement(org.w3c.dom.Element, org.springframework.beans.factory.config.BeanDefinition)
	 * @see NamespaceHandlerSupport#findParserForElement(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 * @see ComponentScanBeanDefinitionParser#parse(org.w3c.dom.Element, org.springframework.beans.factory.xml.ParserContext)
	 * @see ClassPathBeanDefinitionScanner#doScan(java.lang.String...)
	 */
	@Test
	public void test1(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("component-scan.xml");
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}

	/**
	 * 注解方式  @ComponentScan("com.goat.chapter201.common")
	 * @see ConfigurationClassParser#doProcessConfigurationClass(org.springframework.context.annotation.ConfigurationClass, org.springframework.context.annotation.ConfigurationClassParser.SourceClass)
	 * @see ComponentScanAnnotationParser#parse(org.springframework.core.annotation.AnnotationAttributes, java.lang.String)
	 * @see ClassPathBeanDefinitionScanner#doScan(java.lang.String...)
	*/
	@Test
	public void test2(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(MyComponentConfig.class);
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}
}
