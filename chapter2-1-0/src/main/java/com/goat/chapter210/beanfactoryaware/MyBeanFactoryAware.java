package com.goat.chapter210.beanfactoryaware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: BeanFactoryAware 测试
 * @ author  山羊来了
 * @ date 2019/8/10---11:19
 */
@Component
public class MyBeanFactoryAware implements BeanFactoryAware {

	private BeanFactory factory;

	// 这样我们就可以随意取ICO里的对象了
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		factory = beanFactory;
	}

	public BeanFactory getFactory() {
		return factory;
	}
}
