package org.springframework.cglib.item03;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2021/6/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/27---10:41
 */
public class App {

	@Test
	public void test(){

		ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		TestBean bean = ac.getBean(TestBean.class);
		System.out.println(bean);


		Config config = ac.getBean(Config.class);
		System.out.println(config);


	}
}
