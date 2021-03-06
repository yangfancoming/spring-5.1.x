package com.goat.chapter185.item02;

import com.goat.chapter185.common.Dog;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: doit  多个 BeanPostProcessor 的执行顺序？？？
 * @ author  山羊来了
 * @ date 2019/8/17---20:40
 * @see BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
 */
public class App {

	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean2.xml");

	/**
	 * 测试BeanPostProcessor
	 * BeanPostProcessor第1次被调动
	 * BeanPostProcessor第2次被调动
	 * 大家好, 我叫强强, 我今年5岁了
	 */
	@Test
	public void test1() {
		Dog dog = applicationContext.getBean("dog", Dog.class);
		dog.sayHello();
	}
}
