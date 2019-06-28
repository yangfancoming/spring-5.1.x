package com.goat.spring.web.demo.beanpostprocessor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by 64274 on 2019/6/28.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/28---19:28
 */
@Component
public class Car implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	public Car(){
		System.out.println("car instance...");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("ApplicationContextAware...setApplicationContext()");
		this.applicationContext = applicationContext;
		System.out.println("car setApplicationContext...");
		System.out.println(this.applicationContext);
	}
}