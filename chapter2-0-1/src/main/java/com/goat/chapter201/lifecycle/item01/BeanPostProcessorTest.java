package com.goat.chapter201.lifecycle.item01;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by Administrator on 2020/4/21.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/21---13:54
 */
public class BeanPostProcessorTest implements BeanPostProcessor {

	public BeanPostProcessorTest() {
		System.out.println("这是 BeanPostProcessorTest 无参构造函数！！");
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("postProcessBeforeInitialization...Before" + bean +  beanName);
		return null;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("postProcessAfterInitialization...After" + bean +  beanName);
		return null;
	}
}
