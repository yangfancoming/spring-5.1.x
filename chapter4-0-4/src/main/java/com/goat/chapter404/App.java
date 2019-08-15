package com.goat.chapter404;

import com.goat.chapter404.bean.Apple;
import org.springframework.aop.TargetSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/15.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/15---16:44
 */
public class App {

	public static void main(String[] args) throws Exception {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		TargetSource targetSource = (TargetSource) context.getBean("targetSource");
		for (int i = 0; i < 10; i++) {
			Apple apple = (Apple) targetSource.getTarget();
			apple.eat();
		}
	}
}
