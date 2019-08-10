package com.goat.chapter115.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Created by 64274 on 2019/4/22.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/4/22---9:10
 */
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    public MyBeanFactoryPostProcessor() {
        super();
        System.out.println("这是BeanFactoryPostProcessor实现类构造器！！");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("BeanFactoryPostProcessor调用postProcessBeanFactory方法");
        BeanDefinition bd = beanFactory.getBeanDefinition("person");
        bd.getPropertyValues().addPropertyValue("phone", "110");

		BeanDefinition lazyService = beanFactory.getBeanDefinition("lazyService");
		lazyService.setLazyInit(false);
    }

}