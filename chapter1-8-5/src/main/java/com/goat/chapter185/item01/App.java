package com.goat.chapter185.item01;

import com.goat.chapter185.common.Dog;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/17---20:40
 */
public class App {

	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean1.xml");

	protected final Log logger = LogFactory.getLog(getClass());


	/**
	 * 测试BeanFactoryPostProcessor
	 * BeanFactoryPostProcessor第0次被调动
	 * dog属性值:PropertyValues: length=0
	 * 修改dog的作用域为prototype
	 * BeanFactoryPostProcessor第1次被调动
	 * 大家好, 我叫小明, 我今年3岁了
	*/
	@Test
	public void test2() {
		logger.warn("11111111111111");
		Dog dog = applicationContext.getBean("dog", Dog.class);
		dog.sayHello();
	}
}
