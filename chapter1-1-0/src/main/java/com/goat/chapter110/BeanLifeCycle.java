package com.goat.chapter110;


import com.goat.chapter110.bean.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * bean的生命周期：
 * 		bean创建---初始化----销毁的过程

 * BeanPostProcessor.postProcessBeforeInitialization
 * BeanPostProcessor.postProcessAfterInitialization

 *
 * 遍历得到容器中所有的BeanPostProcessor；挨个执行beforeInitialization，
 * 一但返回null，跳出for循环，不会执行后面的postProcessorsBeforeInitialization
 *
 * BeanPostProcessor原理:
 * 1、populateBean(beanName, mbd, instanceWrapper);给bean进行属性赋值
 * 2、initializeBean
 *     1）applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
 *     2）invokeInitMethods(beanName, wrappedBean, mbd);执行自定义初始化方法
 *     3）applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
 *
 *

 * 4）、BeanPostProcessor，两个主要方法如下：
 * 		postProcessBeforeInitialization:
 * 	        在初始化之前工作
 * 	        before InitializingBean#afterPropertiesSet or a custom init-method
 * 	        理解：在上面三者之前调用
 *
 * 		postProcessAfterInitialization:
 * 		    在初始化之后工作
 * 		    after InitializingBean#afterPropertiesSet or a custom init-method
 * 		    理解：在上面三者之后调用
 *
 * Spring底层对 BeanPostProcessor 的使用；
 * 		bean赋值，注入其他组件，@Autowired，生命周期注解功能，@Async, xxxBeanPostProcessor;
 * 	    AutowiredAnnotationBeanPostProcessor
 */
@Configuration
public class BeanLifeCycle {

	@Bean
	public Car car() {
		return new Car();
	}

	@Bean()
	public Human human() {
		return new Man();
	}
}
