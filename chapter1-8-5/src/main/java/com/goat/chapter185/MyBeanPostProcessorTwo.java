package com.goat.chapter185;



import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: 继承BeanPostProcessor接口并重写其方法
 * @ author  山羊来了
 * @ date 2019/8/17---20:38
 */
public class MyBeanPostProcessorTwo implements BeanPostProcessor, Ordered {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("BeanPostProcessor第" + getOrder() + "次被调动");
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	@Override
	public int getOrder() {
		return 2;
	}
}
