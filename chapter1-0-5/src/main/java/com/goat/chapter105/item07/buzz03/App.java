package com.goat.chapter105.item07.buzz03;

import com.goat.chapter105.BaseTest;
import com.goat.chapter105.item07.common.TestService4;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App extends BaseTest {

	ApplicationContext ac = new AnnotationConfigApplicationContext(MyConfig.class);

	@Test
	public void forAll(){look(ac);}

	/**
	 * 4）、自动装配默认一定要将属性赋值好，没有就会报错； 可以使用@Autowired(required=false);
	 *  TestService4 中不加 @Autowired(required = false)  则会报错
	 *  加上之后 意思就是找不到那个bean 就算了 不找了 为null就好了
	*/
	@Test
	public void getBean2(){
		TestService4 testService = ac.getBean(TestService4.class);
		testService.printDao();
	}
}
