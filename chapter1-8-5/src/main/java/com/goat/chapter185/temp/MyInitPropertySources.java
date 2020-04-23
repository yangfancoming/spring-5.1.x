package com.goat.chapter185.temp;

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

	// doit 这个方法到底咋用？
	@Override
	protected void initPropertySources() {
		super.initPropertySources();
		//把"MYSQL_HOST"作为启动的时候必须验证的环境变量
		getEnvironment().setRequiredProperties("MYSQL_HOST");
		System.out.println("MyInitPropertySources#initPropertySources()...");
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
