package com.goat.chapter200.item03;

import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2021/6/16.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/16---16:17
 */
public class App {

	/**
	 * AUTOWIRE_BY_NAME
	 * @see AbstractAutowireCapableBeanFactory#autowireByName(java.lang.String, org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.beans.BeanWrapper, org.springframework.beans.MutablePropertyValues)
	 * @see AbstractAutowireCapableBeanFactory#unsatisfiedNonSimpleProperties(org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.beans.BeanWrapper)
	 * @see AbstractAutowireCapableBeanFactory#applyPropertyValues(java.lang.String, org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.BeanWrapper, org.springframework.beans.PropertyValues)
	 * @see PropertyAccessor#setPropertyValues(org.springframework.beans.PropertyValues)
	 * @see BeanWrapperImpl.BeanPropertyHandler#setValue(java.lang.Object)
	 * @see Method#invoke(java.lang.Object, java.lang.Object...)
	 *
	 * AUTOWIRE_BY_TYPE
	 * @see AbstractAutowireCapableBeanFactory#autowireByType(java.lang.String, org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.beans.BeanWrapper, org.springframework.beans.MutablePropertyValues)
	*/
	@Test
	public void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		UserService userService = ac.getBean(UserService.class);
		userService.query();
	}
}
