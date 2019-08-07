package com.goat.chapter200.base;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;


public class BaseTest {

	public 	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

	public void pre(Class<?> T){
		ctx.register(T);
		ctx.refresh();

		String[] str= ctx.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***" + x));
	}
}
