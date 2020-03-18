package com.goat.chapter105.item07.buzz04;

import com.goat.chapter105.BaseTest;
import com.goat.chapter105.item07.common.TestService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App extends BaseTest {

	ApplicationContext ac = new AnnotationConfigApplicationContext(MyConfig.class);

	@Test
	public void forAll(){look(ac);}

	/**
	 * MyConfig 中增加 @Primary 注解  获取 TestDao{mark='2'}
	 * MyConfig 中干掉 @Primary 注解  获取 TestDao{mark='1'}
	 * 5）、@Primary：让Spring进行自动装配的时候，默认使用首选的bean； 也可以继续使用@Qualifier指定需要装配的bean的名字
	*/
	@Test
	public void getBean2(){
		TestService testService = ac.getBean(TestService.class);
		testService.printDao();
	}


}
