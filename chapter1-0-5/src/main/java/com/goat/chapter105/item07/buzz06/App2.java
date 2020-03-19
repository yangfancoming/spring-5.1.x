package com.goat.chapter105.item07.buzz06;

import com.goat.chapter105.BaseTest;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App2 extends BaseTest {

	ApplicationContext ac2 = new AnnotationConfigApplicationContext(MyConfig.class);

	/**
	 *  4）、自定义组件想要使用Spring容器底层的一些组件（ApplicationContext，BeanFactory，xxx）；
	 *
	 */
	@Test
	public void getBean3(){

	}

}
