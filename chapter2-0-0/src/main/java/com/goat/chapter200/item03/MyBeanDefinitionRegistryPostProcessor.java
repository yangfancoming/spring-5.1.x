package com.goat.chapter200.item03;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2021/6/16.
 * @ Description: 通过自定义BeanDefinitionRegistryPostProcessor设置UserService的属性自动装配模式为byName
 * @ author  山羊来了
 * @ date 2021/6/16---16:19
 */
@Component
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		GenericBeanDefinition beanDefinition = (GenericBeanDefinition) registry.getBeanDefinition("userService");
//		beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
//		beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		registry.registerBeanDefinition("userService",beanDefinition);
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}
}
