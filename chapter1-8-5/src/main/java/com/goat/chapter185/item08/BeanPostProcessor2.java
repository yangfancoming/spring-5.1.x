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
public class BeanPostProcessor2 implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	BeanPostProcessor2...Before" + bean +  beanName);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	BeanPostProcessor2...After" + bean +  beanName);
		return bean;
	}
}
