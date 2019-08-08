package com.goat.chapter400.xml;


import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

/**
 * Created by 64274 on 2019/8/1.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/1---9:41
 */
public class App {

	ClassPathResource resource  = new ClassPathResource("MyAop.xml");
	/**  测试 */
	@Test
	public void test3(){
		// 创建IOC配置文件的抽象资源，即配置文件
		// 创建一个BeanFactory,这里使用DefaultListableBeanFactory
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		reader.loadBeanDefinitions(resource);
	}


}
