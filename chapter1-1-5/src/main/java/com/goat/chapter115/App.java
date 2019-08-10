package com.goat.chapter115;


import com.goat.chapter115.service.WhatService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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

	ClassPathResource resource  = new ClassPathResource("application.xml");
	ApplicationContext context = new ClassPathXmlApplicationContext("classpath:application.xml");

	/** 接口 测试 */
	@Test
	public void test1(){
		WhatService whatService = context.getBean(WhatService.class);
		System.out.println(whatService.getMessage()); // 这句将输出: hello world
	}

	/** 抽象bean  测试 */
	@Test
	public void test2(){
		Object abstractBeanTest = context.getBean("abstractBeanTest");
		System.out.println(abstractBeanTest);
	}
}
