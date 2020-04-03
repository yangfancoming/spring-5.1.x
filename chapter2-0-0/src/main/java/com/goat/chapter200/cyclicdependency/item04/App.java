package com.goat.chapter200.cyclicdependency.item04;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/4/3.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/3---15:15
 */
public class App {



	ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);

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
