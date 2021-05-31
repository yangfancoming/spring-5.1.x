package com.goat.chapter105.item07.buzz06;

import com.goat.chapter105.BaseTest;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App2 extends BaseTest {

	ApplicationContext ac = new AnnotationConfigApplicationContext(MyConfig.class);

	/**
	 *  4）、自定义组件想要使用Spring容器底层的一些组件（ApplicationContext，BeanFactory，xxx）；
	 */
	@Test
	public void getBean3(){
		look(ac);
		// 证明：当前的spring容器 ac 与 Red类setApplicationContext方法中传入的是同一个容器
		System.out.println(ac);
	}

}
