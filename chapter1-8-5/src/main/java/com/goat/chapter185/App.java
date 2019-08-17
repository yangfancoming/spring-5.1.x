package com.goat.chapter185;

import org.junit.After;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by 64274 on 2019/8/17.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/17---20:40
 */
public class App {

	ApplicationContext applicationContext = new ClassPathXmlApplicationContext("bean1.xml");
	@After
	public void after() {
		System.out.println("\n========测试方法结束=======\n");
	}
	/**
	 * ========测试方法开始=======
	 * BeanPostProcessor第1次被调动
	 * BeanPostProcessor第2次被调动
	 * 大家好, 我叫强强, 我今年5岁了
	 */
	@Test
	public void test1() {
		// 测试BeanPostProcessor
		Dog dog = applicationContext.getBean("dog", Dog.class);
		dog.sayHello();
	}

/**
 * BeanFactoryPostProcessor第0次被调动
 * dog属性值:PropertyValues: length=0
 * 修改dog的作用域为prototype
 * BeanFactoryPostProcessor第1次被调动
 * 大家好, 我叫小明, 我今年3岁了
*/
	@Test
	public void test2() {
		// 测试BeanFactoryPostProcessor
		Dog dog = applicationContext.getBean("dog", Dog.class);
		dog.sayHello();
	}

}
