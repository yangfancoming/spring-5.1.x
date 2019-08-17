package com.goat.chapter115.bean;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by 64274 on 2019/8/17.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/17---20:14
 */
public class LifeCycleBean implements BeanNameAware, BeanFactoryAware, ApplicationContextAware, InitializingBean, DisposableBean {

	// 姓名
	private String name;
	// 年龄
	private int age;

	@Override
	public void setBeanName(String name) {
		System.out.println("01-->BeanNameAware接口被调用了, 获取到的beanName:" + name);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		System.out.println("02-->BeanFactoryAware接口被调用了");
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("03-->ApplicationContextAware接口被调用了");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.out.println("05-->InitializingBean接口被调用了");
	}

	public void myInit() {
		System.out.println("06-->myInit方法被调用了");
	}

	@Override
	public void destroy() throws Exception {
		System.out.println("09-->DisposableBean接口被调用了");
	}

	public void myDestroy() {
		System.out.println("10-->自定义destroy-method方法被调动了");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "MyLifeCycleBean{" + "name='" + name + '\'' + ", age=" + age + '}';
	}
}