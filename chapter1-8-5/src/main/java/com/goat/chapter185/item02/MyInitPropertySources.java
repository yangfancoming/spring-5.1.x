package com.goat.chapter185.item02;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2020/4/6.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/6---18:49
 */
@Component
public class MyInitPropertySources extends AbstractApplicationContext {

	@Override
	protected void initPropertySources() {
		System.out.println("goat initPropertySources...........");
	}

	@Override
	protected void refreshBeanFactory() throws BeansException, IllegalStateException {

	}

	@Override
	protected void closeBeanFactory() {

	}

	@Override
	public ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException {
		return null;
	}
}
