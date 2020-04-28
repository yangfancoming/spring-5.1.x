package com.goat.chapter650;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Created by Administrator on 2020/4/28.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/28---14:05
 */
public class SpringMybatisTestApp1 {

	@Test
	public void test() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("SpringMybatisTestApp1.xml");
		System.out.println(context);
	}

}
