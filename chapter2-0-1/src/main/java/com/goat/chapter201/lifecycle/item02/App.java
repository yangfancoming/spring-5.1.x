package com.goat.chapter201.lifecycle.item02;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * Created by Administrator on 2020/5/6.
 * @ Description:
 * @ author  山羊来了
 * @ date 2020/5/6---20:59
 */
public class App {

	ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);

	@Test
	public void test(){
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***	 " + x));
	}

	@Test
	public void test1(){
		Baby baby = ac.getBean(Baby.class);
		baby.hello();

		Daddy daddy = ac.getBean(Daddy.class);
		daddy.dosomething();
	}
}
