package com.goat.chapter202.item02;


import com.goat.chapter202.item02.config.JavaConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * Created by 64274 on 2019/8/12.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/12---20:45
 */
public class App {

	ApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);

	/**
	 * 显示容器中的所有bean
	 */
	@Before
	public void test2(){
		String[] str= context.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x-> System.out.println("***--------"+x));
	}

	@Test
	public void test(){

	}
}
