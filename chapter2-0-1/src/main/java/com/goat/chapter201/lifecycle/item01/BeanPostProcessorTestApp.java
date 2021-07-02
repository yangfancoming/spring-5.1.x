package com.goat.chapter201.lifecycle.item01;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class BeanPostProcessorTestApp {

	@Test
	public void test2() {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("BeanPostProcessorTest.xml");
		System.out.println(ac);
		ac.close();
	}
}
