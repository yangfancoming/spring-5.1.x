package com.goat.chapter200.cyclicdependency.item04;

import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/4/3.
 *
 * @ Description: 延时加载方式  解决循环依赖
 * @ author  山羊来了
 * @ date 2020/4/3---15:15
 */
public class App {



	AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);

	/**
	 * 因为是延迟加载 所以容器启动正常
	 */
	@Test
	public void test(){
		System.out.println(ac);
	}

	// 容器启动后  真正调用时报错：org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'a':
	@Test
	public void test1(){
		A bean = ac.getBean(A.class);
		System.out.println(bean);
	}
}
