package com.goat.chapter185.item08;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2021/6/11.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/11---20:08
 */
@Component
public class BeanPostProcessor1 implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	BeanPostProcessor1...Before	"  + beanName);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	BeanPostProcessor1...After	" + beanName );
		return bean;
	}
}
