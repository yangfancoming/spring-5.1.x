package com.goat.spring.web.demo.beanpostprocessor;



import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 后置处理器：初始化前后进行处理工作
 * 需要将后置处理器加入到容器中
 * Spring容器中加入MyBeanPostProcessor之后，针对容器中每个创建的Bean对象（Spring自身创建的Bean和应用程序创建的Bean），都会回调这两个方法
 *
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("后置处理器  befor ..." + beanName + "..." + bean);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("后置处理器  after ..." + beanName + "..." + bean);
		return bean;
	}

}