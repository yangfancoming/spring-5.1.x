package com.goat.chapter185.item08;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**

 */
@Component
public class BeanFactoryPostProcessor1 implements BeanFactoryPostProcessor, Ordered {

	/**
	 * 修改bean的bean
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinition bd = beanFactory.getBeanDefinition("normal baby");
		MutablePropertyValues pv = bd.getPropertyValues();
		pv.addPropertyValue("name", "明明");
		bd.setScope(BeanDefinition.SCOPE_SINGLETON);
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@	BeanFactoryPostProcessor1..."  +  "改名为 明明");
	}

	@Override
	public int getOrder() {
		return 1;
	}
}
