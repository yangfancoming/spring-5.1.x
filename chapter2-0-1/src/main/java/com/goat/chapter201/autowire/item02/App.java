package com.goat.chapter201.autowire.item02;
import com.goat.chapter201.autowire.MongoDao;
import com.goat.chapter201.autowire.MysqlDao;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/**
 * 自动注入  编程版
 */
public class App {

	// 无 @Autowired 的service
	/**
	 * 将autowire="byName"配置 设置到AbstractBeanDefinition中，入口：
	 * @see BeanDefinitionParserDelegate#parseBeanDefinitionAttributes(org.w3c.dom.Element, java.lang.String, org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.factory.support.AbstractBeanDefinition)
	 * 出口
	 * @see AbstractAutowireCapableBeanFactory#populateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, org.springframework.beans.BeanWrapper)
	 * @see AbstractAutowireCapableBeanFactory#autowireByName(java.lang.String, org.springframework.beans.factory.support.AbstractBeanDefinition, org.springframework.beans.BeanWrapper, org.springframework.beans.MutablePropertyValues)
	 */
	@Test
	public void test1(){
		// 准备rbd
		RootBeanDefinition service = new RootBeanDefinition(Service.class);
		service.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
		// 创建工厂
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
		// 向容器中注册bd
		beanFactory.registerBeanDefinition("service",service);
		// 注册到一级单例缓冲池
		beanFactory.registerSingleton("mongoDao",new MongoDao());
		beanFactory.registerSingleton("mysqlDao",new MysqlDao());
		// 触发 autowireByName 自动注入
		Service bean = beanFactory.getBean(Service.class);
		// 验证结果
		Assert.assertNotNull(bean.getMongoDao());
		Assert.assertNotNull(bean.getMysqlDao());
	}
}