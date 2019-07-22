package com.goat.spring.postprocessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;


@Component
public class CompentInteceptor implements BeanPostProcessor ,InstantiationAwareBeanPostProcessor {

	/*  BeanPostProcessor  */
	@Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(beanName.equals("compent")) {
            System.out.println("BeanPostProcessor........." + beanName + "的后置处理器的Before");
        }
        return bean;
    }

	@Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(beanName.equals("compent")) {
			System.out.println("BeanPostProcessor........." + beanName + "的后置处理器的After");
        }
        return bean;
    }

    /*  InstantiationAwareBeanPostProcessor  */
	@Override
	public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		if(beanName.equals("compent")) {
			System.out.println("InstantiationAwareBeanPostProcessor...." + beanName + "实例化之前");
		}
		return null;
	}

	@Override
	public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		if(beanName.equals("compent")) {
			System.out.println("InstantiationAwareBeanPostProcessor...." + beanName + "实例化之后");
		}
		return false;
	}


	@Override
	public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
		return null;
	}
}
