package com.goat.chapter201.componentscan;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

/**
 * Created by Administrator on 2020/4/24.
 *
 * @ Description: <context:component-scan 标签解析
 * @ author  山羊来了
 * @ date 2020/4/24---14:28
 */
public class App {

	/**
	 * 传统标签 <component-scan> 可以扫描到 com.goat.chapter105.common 包下的
	 * 其中6个类中 2个没有注解 因此只有4个会被加载到spring的容器中
	 * ***---***	 testController
	 * ***---***	 testDao
	 * ***---***	 testFilterDao
	 * ***---***	 testService
	 *  调用堆栈：
	 * parse:68, ComponentScanBeanDefinitionParser (org.springframework.context.annotation)
	 * parse:54, NamespaceHandlerSupport (org.springframework.beans.factory.xml)
	 * parseCustomElement:1218, BeanDefinitionParserDelegate (org.springframework.beans.factory.xml)
	 * parseCustomElement:1203, BeanDefinitionParserDelegate (org.springframework.beans.factory.xml)
	 * parseBeanDefinitions:181, DefaultBeanDefinitionDocumentReader (org.springframework.beans.factory.xml)
	 * doRegisterBeanDefinitions:143, DefaultBeanDefinitionDocumentReader (org.springframework.beans.factory.xml)
	 * registerBeanDefinitions:80, DefaultBeanDefinitionDocumentReader (org.springframework.beans.factory.xml)
	 * registerBeanDefinitions:430, XmlBeanDefinitionReader (org.springframework.beans.factory.xml)
	 * doLoadBeanDefinitions:336, XmlBeanDefinitionReader (org.springframework.beans.factory.xml)
	 * loadBeanDefinitions:286, XmlBeanDefinitionReader (org.springframework.beans.factory.xml)
	 * loadBeanDefinitions:483, XmlBeanDefinitionReader (org.springframework.beans.factory.xml)
	 * loadBeanDefinitions:196, AbstractBeanDefinitionReader (org.springframework.beans.factory.support)
	 * loadBeanDefinitions:142, AbstractBeanDefinitionReader (org.springframework.beans.factory.support)
	 * loadBeanDefinitions:203, AbstractBeanDefinitionReader (org.springframework.beans.factory.support)
	 * loadBeanDefinitions:211, AbstractBeanDefinitionReader (org.springframework.beans.factory.support)
	 * loadBeanDefinitions:109, AbstractXmlApplicationContext (org.springframework.context.support)
	 * loadBeanDefinitions:73, AbstractXmlApplicationContext (org.springframework.context.support)
	 * refreshBeanFactory:107, AbstractRefreshableApplicationContext (org.springframework.context.support)
	 * obtainFreshBeanFactory:582, AbstractApplicationContext (org.springframework.context.support)
	 * refresh:448, AbstractApplicationContext (org.springframework.context.support)
	 * <init>:126, ClassPathXmlApplicationContext (org.springframework.context.support)
	 * <init>:70, ClassPathXmlApplicationContext (org.springframework.context.support)
	 * test1:28, App (com.goat.chapter201.componentscan)
	 */
	@Test
	public void test1(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("component-scan.xml");
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}

	/**
	 * 注解方式  @ComponentScan("com.goat.chapter201.common")
	 *  调用堆栈：
	 * doProcessConfigurationClass:244, ConfigurationClassParser (org.springframework.context.annotation)
	 * processConfigurationClass:227, ConfigurationClassParser (org.springframework.context.annotation)
	 * parse:184, ConfigurationClassParser (org.springframework.context.annotation)
	 * parse:152, ConfigurationClassParser (org.springframework.context.annotation)
	 * processConfigBeanDefinitions:300, ConfigurationClassPostProcessor (org.springframework.context.annotation)
	 * postProcessBeanDefinitionRegistry:217, ConfigurationClassPostProcessor (org.springframework.context.annotation)
	 * invokeBeanDefinitionRegistryPostProcessors:233, PostProcessorRegistrationDelegate (org.springframework.context.support)
	 * invokeBeanFactoryPostProcessors:68, PostProcessorRegistrationDelegate (org.springframework.context.support)
	 * invokeBeanFactoryPostProcessors:656, AbstractApplicationContext (org.springframework.context.support)
	 * refresh:472, AbstractApplicationContext (org.springframework.context.support)
	 * <init>:71, AnnotationConfigApplicationContext (org.springframework.context.annotation)
	 * test2:61, App (com.goat.chapter201.componentscan)
	*/
	@Test
	public void test2(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(MyComponentConfig.class);
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}
}
