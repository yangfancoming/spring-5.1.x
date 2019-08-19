package com.goat.chapter604;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/19.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/19---19:59
 */
public class App {

	@Test
	public void test2() {
		// 基于tx标签的声明式事物
		ApplicationContext ctx = new ClassPathXmlApplicationContext("bean.xml");
		AccountServiceImpl studentService = ctx.getBean("accountService", AccountServiceImpl.class);
		studentService.save();
	}
}
