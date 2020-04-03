package com.goat.chapter200.cyclicdependency.item03;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/4/3.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/3---15:15
 */
public class App {

	/**
	 * 项目启动成功，能够正常work
	*/
	@Test
	public void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		System.out.println(ac);
		A bean = ac.getBean(A.class);
		System.out.println(bean);
	}
}
