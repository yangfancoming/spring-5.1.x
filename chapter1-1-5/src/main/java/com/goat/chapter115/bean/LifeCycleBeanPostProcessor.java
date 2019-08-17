package com.goat.chapter115.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by 64274 on 2019/8/17.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/17---20:14
 */
public class LifeCycleBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof LifeCycleBean) {
			System.out.println("04-->调用postProcessBeforeInitialization方法, 获取到的beanName: " + beanName);
			((LifeCycleBean) bean).setName("李四");
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof LifeCycleBean) {
			System.out.println("07-->调用postProcessAfterInitialization, 获取到的beanName: " + beanName);
			((LifeCycleBean) bean).setAge(30);
		}
		return bean;
	}

}