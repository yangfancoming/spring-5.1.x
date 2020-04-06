package com.goat.chapter185.temp;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * Created by Administrator on 2020/4/6.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/6---18:45
 */
public class App {

	ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);

	@Test
	public void test(){
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}
}
