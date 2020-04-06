package com.goat.chapter185.item01;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/17---20:39
 */
public class MyBeanFactoryPostProcessorOne implements BeanFactoryPostProcessor, Ordered {

	/**
	 * 修改dog的name属性值
	 * 修改dog的作用域
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("BeanFactoryPostProcessor第" + getOrder() + "次被调动");
		BeanDefinition bd = beanFactory.getBeanDefinition("dog");
		if (null != bd) {
			System.out.println("dog属性值:" + bd.getPropertyValues().toString());
			MutablePropertyValues pv = bd.getPropertyValues();
			if (pv.contains("name")) {
				System.out.println("修改dog的name属性值为强强");
				pv.addPropertyValue("name", "强强");
			}
			System.out.println("修改dog的作用域为prototype\n");
			bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		}
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
