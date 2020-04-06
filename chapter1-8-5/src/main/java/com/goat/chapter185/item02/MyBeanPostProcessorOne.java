package com.goat.chapter185.item02;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: 继承BeanPostProcessor接口并重写其方法
 * @ author  山羊来了
 * @ date 2019/8/17---20:37
 */
import com.goat.chapter185.common.Dog;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

public class MyBeanPostProcessorOne implements BeanPostProcessor, Ordered {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		System.out.println("BeanPostProcessor第" + getOrder() + "次被调动\n");
		if (bean instanceof Dog) {
			((Dog) bean).setName("强强");
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof Dog) {
			((Dog) bean).setAge(5);
		}
		return bean;
	}

	@Override
	public int getOrder() {
		return 1;
	}
}