package com.goat.chapter201.autowire.item02;

import com.goat.chapter201.autowire.MongoDao;
import com.goat.chapter201.autowire.MysqlDao;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * 自动注入  编程式 注入版
 */
public class App {

	@Test
	public void test1(){
		RootBeanDefinition service = new RootBeanDefinition(Service.class);
		service.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);

		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		beanFactory.registerSingleton("mongoDao",new MongoDao());
		beanFactory.registerSingleton("mysqlDao",new MysqlDao());
		beanFactory.registerBeanDefinition("service",service);

		Service bean = beanFactory.getBean(Service.class);
		Assert.assertNotNull(bean.getMongoDao());
		Assert.assertNotNull(bean.getMysqlDao());
	}

}