package com.goat.chapter201.condition;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ConditionEvaluator;
import org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader;
import org.springframework.context.annotation.ConfigurationClassParser;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: conditionEvaluator
 * @ author  山羊来了
 * @ date 2020/3/17---13:24
 * {@link ConditionEvaluator}  处理 @Condition 注解的类
 * 整个spring源码 总共有三处调用：
 * 1. 解析@Bean方法
 * @see ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForBeanMethod(org.springframework.context.annotation.BeanMethod)
 * 2. 解析@Configuration类
 * @see ConfigurationClassParser#processConfigurationClass(org.springframework.context.annotation.ConfigurationClass)
 * 3. 解析被@ComponentScan注解的类
 * @see ConfigurationClassParser#doProcessConfigurationClass(org.springframework.context.annotation.ConfigurationClass, ConfigurationClassParser.SourceClass)
 */
public class App {

	/**
	 * @Conditional 作用在主配置类上  返回条件为 true
	 * 容器将会注入主配置类中的bean
	 */
	@Test
	public void test1(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ConditionalClassConfig1.class);
		Assert.assertTrue(ac.containsBean("zoo"));
		Assert.assertTrue(ac.containsBean("foo"));
	}

	/**
	 * @Conditional 作用在主配置类上 返回条件为 false
	 * 容器将不会注入主配置类中的bean
	*/
	@Test
	public void test2(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ConditionalClassConfig2.class);
		Assert.assertFalse(ac.containsBean("zoo"));
		Assert.assertFalse(ac.containsBean("foo"));
	}

	/**
	 * @Conditional 作用在方法上
	 */
	@Test
	public void method(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ConditionalMethodConfig.class);
		Assert.assertTrue(ac.containsBean("bill"));
		Assert.assertFalse(ac.containsBean("linus"));
	}
}
