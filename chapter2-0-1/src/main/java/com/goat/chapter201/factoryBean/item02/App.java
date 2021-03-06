package com.goat.chapter201.factoryBean.item02;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Created by 64274 on 2019/8/17.
 * @ Description: FactoryBean 接口学习
 * @ author  山羊来了
 * @ date 2019/8/17---19:18
 *  *  我们知道，
 *  *  1.实现了FactoryBean的bean会调用它的getObject方法创建bean，
 *  *  2.实现了InitializingBean的bean会在属性填充完成之后调用它的afterPropertiesSet方法
 */
public class App {

	private XmlBeanFactory xmlBeanFactory;

	@Before
	public void initXmlBeanFactory() {
		System.setProperty("spring.profiles.active", "dev");
		System.out.println("\n========测试方法开始=======\n");
		xmlBeanFactory = new XmlBeanFactory(new ClassPathResource("bean5.xml"));
	}

	// 获取到的是StudentFactoryBean产生的实例，也就是Student类的实例；
	@Test
	public void test1() {
		Student student = (Student) xmlBeanFactory.getBean("student");
		System.out.println(student);
	}

	// 获取到的是StudentFactoryBean自己的实例。
	@Test
	public void test2() {
		StudentFactoryBean bean = (StudentFactoryBean) xmlBeanFactory.getBean("&student");
		System.out.println(bean);
	}
	/**
	 * 返回不同Bean的实例
	 * 新建了家具接口和桌子、椅子实现类，通过xml文件配置，
	 * 在FurnitureFactoryBean的getObject方法进行判断，并返回不同的家具类型实例。
	 * */
	@Test
	public void test12() {
		// FactoryBean简单工厂测试
		Furniture furniture = xmlBeanFactory.getBean("furniture", Furniture.class);
		furniture.sayHello();
	}

	@After
	public void after() {
		System.out.println("\n========测试方法结束=======\n");
	}
}

