package com.goat.chapter201.componentscan;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
	 * 其中6个类中 2个没有注解 因此只有4个会被加载到spring的容器中
	 * ***---***	 testController
	 * ***---***	 testDao
	 * ***---***	 testFilterDao
	 * ***---***	 testService
	 */
	@Test
	public void test1(){
		ApplicationContext ac = new ClassPathXmlApplicationContext("component-scan.xml");
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}

	/**
	 * 注解方式  @ComponentScan("com.goat.chapter201.common")
	*/
	@Test
	public void test2(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(MyComponentConfig.class);
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}
}
