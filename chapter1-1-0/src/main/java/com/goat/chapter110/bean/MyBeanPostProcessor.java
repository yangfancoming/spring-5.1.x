package com.goat.chapter110.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


/**
 * 后置处理器
*/
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

	public MyBeanPostProcessor() {
		System.out.println("MyBeanPostProcessor  构造函数调用！！");
	}

	/**
     * before InitializingBean#afterPropertiesSet or a custom init-method
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor  Before... " + beanName + " => " + bean);
        return bean;
    }

    /**
     * after InitializingBean#afterPropertiesSet or a custom init-method
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("MyBeanPostProcessor  After... " + beanName + " => " + bean);
        return bean;
    }

}

