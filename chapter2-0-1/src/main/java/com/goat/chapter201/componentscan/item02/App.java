package com.goat.chapter201.componentscan.item02;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import java.util.Arrays;

/**
 * Created by Administrator on 2020/4/24.
 * @ Description: 测试 component-scan 的 value 属性 注解版
 * @ author  山羊来了
 * @ date 2020/4/24---14:28
 */
public class App {

	/**
	 * 注解方式  @ComponentScan("com.goat.chapter201.common")
	 * @see ConfigurationClassParser#doProcessConfigurationClass(ConfigurationClass, ConfigurationClassParser.SourceClass)
	 * @see ComponentScanAnnotationParser#parse(org.springframework.core.annotation.AnnotationAttributes, String)
	 * @see ClassPathBeanDefinitionScanner#doScan(String...)
	*/
	@Test
	public void test2(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(MyComponentConfig.class);
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}
}
