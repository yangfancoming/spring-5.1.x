package com.goat.spring.demo;

import com.goat.spring.demo.config.AppConfig;
import com.goat.spring.demo.service.MessageService;
import com.goat.spring.demo.service.TestService;
import com.goat.spring.demo.service.TransactionService;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

/**
 * Created by 64274 on 2019/8/1.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/1---9:41
 */
@ActiveProfiles("test")
public class App {

	/** 事务测试 */
	@Test
	public void test(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
		TransactionService bean = ctx.getBean(TransactionService.class);
		System.out.println(bean.test1("hoho"));
	}

	/** service 测试 */
	@Test
	public void test1(){
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
		TestService bean = ctx.getBean(TestService.class);
		bean.test();
	}

	ClassPathResource resource  = new ClassPathResource("application.xml");
	/**  测试 */
	@Test
	public void test3(){
		// 创建IOC配置文件的抽象资源，即配置文件
		// 创建一个BeanFactory,这里使用DefaultListableBeanFactory
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		// 创建一个载入BeanDefinition的读取器，这里使用XmlBeanDefinitionReader 来载入XML文件形式的BeanDefinition
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		// 从定义好的资源位置读取配置信息，具体的解析过程由XmlBeanDefinitionReader 来完成
		reader.loadBeanDefinitions(resource);
		MessageService bean = factory.getBean(MessageService.class);
		System.out.println(bean.getMessage()); // 这句将输出: hello world
	}

	/**  测试 */
	@Test
	public void test4(){
		XmlBeanFactory factory = new XmlBeanFactory(resource);
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		reader.loadBeanDefinitions(resource);
		MessageService bean = factory.getBean(MessageService.class);
		System.out.println(bean.getMessage());
	}
}
