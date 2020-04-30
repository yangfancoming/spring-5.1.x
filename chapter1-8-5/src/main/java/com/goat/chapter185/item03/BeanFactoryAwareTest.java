package com.goat.chapter185.item03;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Created by Administrator on 2020/4/21.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/21---13:42
 */
public class BeanFactoryAwareTest implements BeanFactoryAware {

	private BeanFactory factory;

	// 这里可以拿到 spring容器！
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("setBeanFactory..." +  beanFactory);
		factory = beanFactory;
	}

	public BeanFactory getFactory() {
		return factory;
	}
}
