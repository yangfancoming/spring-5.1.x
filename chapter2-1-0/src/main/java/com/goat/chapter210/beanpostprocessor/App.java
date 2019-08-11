package com.goat.chapter210.beanpostprocessor;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/10---11:28
 */
@ComponentScan("com.goat.chapter210.beanpostprocessor")
public class App {

	/**  测试 BeanPostProcessor   */
	@Test
	public void test1(){
		ApplicationContext context = new AnnotationConfigApplicationContext(App.class);
		System.out.println(context);
	}


}
