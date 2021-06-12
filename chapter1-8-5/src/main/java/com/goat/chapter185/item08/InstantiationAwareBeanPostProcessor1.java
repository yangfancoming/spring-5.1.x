package com.goat.chapter185.item08;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2021/6/12.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/12---18:52
 */
@Component
public class InstantiationAwareBeanPostProcessor1 implements InstantiationAwareBeanPostProcessor {

	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	InstantiationAwareBeanPostProcessor...Before"  +  beanName);
		return null;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	InstantiationAwareBeanPostProcessor...After"  +  beanName);
		return true;
	}

	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	InstantiationAwareBeanPostProcessor...postProcessProperties"  +  beanName);
		return null;
	}
}
