package com.goat.chapter201.extend;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.AbstractPropertyResolver;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Created by Administrator on 2021/7/11.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/7/11---12:43
 */
public class MyApplicationContext extends ClassPathXmlApplicationContext {

	public MyApplicationContext(String configLocation) throws BeansException {
		super(configLocation);
	}

	/**
	 * 入口 设置自定义属性
	 * @see AbstractPropertyResolver#setRequiredProperties(java.lang.String...)
	 * 出口  对设置的属性进行校检
	 * @see AbstractPropertyResolver#validateRequiredProperties()
	*/
	@Override
	protected void initPropertySources() {
		ConfigurableEnvironment environment = getEnvironment();
		environment.setRequiredProperties("username");
	}

	/**
	 * 这就是扩展的魅力
	 * @see AbstractRefreshableApplicationContext#refreshBeanFactory()
	 * @see AbstractRefreshableApplicationContext#customizeBeanFactory(org.springframework.beans.factory.support.DefaultListableBeanFactory)
	*/
	@Override
	protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
		super.setAllowBeanDefinitionOverriding(true);
		super.setAllowCircularReferences(true);
		super.customizeBeanFactory(beanFactory);
	}
}
