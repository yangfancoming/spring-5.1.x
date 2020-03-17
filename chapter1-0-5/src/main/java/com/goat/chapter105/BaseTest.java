package com.goat.chapter105;

import org.springframework.context.ApplicationContext;

import java.util.Arrays;

/**
 * Created by Administrator on 2020/3/17.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/17---13:54
 */
public class BaseTest {

	public void look(ApplicationContext ac){
		String[] str= ac.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***" + x));
	}
}
