package com.goat.chapter201.propertysource;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ConfigurationClassParser;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.support.PropertySourceFactory;

/**
 * Created by Administrator on 2021/7/8.
 * @ Description: 学习 @PropertySource 注解
 * @ author  山羊来了
 * @ date 2021/7/8---10:11
 */
public class App {

	ApplicationContext ac = new AnnotationConfigApplicationContext(Config.class);

	/**
	 * 处理 @PropertySource 注解
	 * @see ConfigurationClassParser#processPropertySource(org.springframework.core.annotation.AnnotationAttributes)
	 * @see PropertyResolver#resolveRequiredPlaceholders(java.lang.String)
	 * @see PropertySourceFactory#createPropertySource(java.lang.String, org.springframework.core.io.support.EncodedResource)
	 * @see ConfigurationClassParser#addPropertySource(org.springframework.core.env.PropertySource)
	*/
	@Test
	public void test(){

	}
}
