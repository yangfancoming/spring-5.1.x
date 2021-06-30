package com.goat.chapter201.componentscan.item03;


import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;


/**
 * Created by Administrator on 2020/3/17.
 * @ Description: 注解方式 获取bean
 * @ author  山羊来了
 * @ date 2020/3/17---13:24
 */
public class App {


	@Test
	public void ComponentScans1(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentScansIncludeFiltersConfig.class);
		after(ac);
	}

	@Test
	public void ComponentScans(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentScansExcludeFiltersConfig.class);
		after(ac);
	}

	public void after(ApplicationContext ac){
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}
}
