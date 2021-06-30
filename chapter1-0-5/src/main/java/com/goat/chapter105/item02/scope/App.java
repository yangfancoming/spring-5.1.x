package com.goat.chapter105.item02.scope;

import com.goat.chapter105.model.Person;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

/**
 * Created by Administrator on 2021/6/29.
 * @ Description: @Scope 注解源码位置
 * @ author  山羊来了
 * @ date 2021/6/29---21:23
 */
public class App {

	/**
	 * 测试 @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) 属性
	 * @see Config#person()
	 * 源码位置
	 * @see AbstractBeanFactory#doGetBean(java.lang.String, java.lang.Class, java.lang.Object[], boolean)
	 * 走 else if (mbd.isPrototype()) 分支，不走缓存，所以每次都是新建bean实例。
	*/
	@Test
	public void test(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);
		Person person1 = ac.getBean(Person.class);
		Person person2 = ac.getBean(Person.class);
		Assert.assertNotSame(person1,person2);
	}

	/**
	 * 测试 @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS) 属性
	 * @see Config2#person()
	 * @see ClassPathBeanDefinitionScanner#doScan(java.lang.String...)
	 * @see ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)
	 * @see ScopeMetadataResolver#resolveScopeMetadata(org.springframework.beans.factory.config.BeanDefinition)
	 * @see AnnotationConfigUtils#applyScopedProxyMode(org.springframework.context.annotation.ScopeMetadata, org.springframework.beans.factory.config.BeanDefinitionHolder, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 * @see ConfigurationClassParser#retrieveBeanMethodMetadata(org.springframework.context.annotation.ConfigurationClassParser.SourceClass)
	 * @see ConfigurationClass#addBeanMethod(org.springframework.context.annotation.BeanMethod) 输入
	 * @see ConfigurationClassBeanDefinitionReader#loadBeanDefinitions(java.util.Set)
	 * @see ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForBeanMethod(org.springframework.context.annotation.BeanMethod)
	 * @see ScopedProxyCreator#createScopedProxy(org.springframework.beans.factory.config.BeanDefinitionHolder, org.springframework.beans.factory.support.BeanDefinitionRegistry, boolean)
	 */
	@Test
	public void test2(){
		ApplicationContext ac1 = new AnnotationConfigApplicationContext(Config.class);
		Person person1 = ac1.getBean(Person.class);
		System.out.println(person1); // person1 是真实的对象
		ApplicationContext ac2 = new AnnotationConfigApplicationContext(Config2.class);
		Person person2 = ac2.getBean(Person.class);
		System.out.println(person2); // person2 是被cglib代理的对象
	}
}
