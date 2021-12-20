package com.goat.chapter201.lifecycle.item02;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;


/**
 * Created by Administrator on 2021/12/18.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/12/18---17:05
 */

public class MyBeanDefinitionRegistryPostProcessor2 implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		BeanDefinition person = registry.getBeanDefinition("person");
		MutablePropertyValues propertyValues = person.getPropertyValues();
		System.out.println(propertyValues);
		propertyValues.add("name","pipen");
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}
}
