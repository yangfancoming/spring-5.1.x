package com.goat.chapter200.item04;


import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * Created by Administrator on 2021/6/16.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/16---16:17
 */
public class App {

	/**
	 * AUTOWIRE_BY_NAME
	*/
	@Test
	public void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
		GoatPlayer goatPlayer = ac.getBean(GoatPlayer.class);
		goatPlayer.insert();
	}
}
