package com.goat.chapter185.item;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.stereotype.Component;


@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		AbstractBeanDefinition bd = (AbstractBeanDefinition) beanFactory.getBeanDefinition("person");
		ConstructorArgumentValues cv = new ConstructorArgumentValues();
		// 容器会根据此处指定的参数位置、类型、个数来选择对应的构造函数去创建对象
		cv.addIndexedArgumentValue(1,"18");
		cv.addIndexedArgumentValue(0,18);
		bd.setConstructorArgumentValues(cv);
		// 改变注入方式
		bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
	}

}
