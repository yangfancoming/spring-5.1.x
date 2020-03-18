package com.goat.chapter105.item07.buzz02;

import com.goat.chapter105.BaseTest;
import com.goat.chapter105.item07.common.TestService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App extends BaseTest {

	ApplicationContext ac = new AnnotationConfigApplicationContext(MyConfig.class);

	@Test
	public void forAll(){look(ac);}

	//doit  容器中有2个 TestDao bean 一个mark为1 一个mark为2 为啥他注入的是2的？
	@Test
	public void getBean2(){
		TestService testService = ac.getBean(TestService.class);
		testService.printDao();
	}
}
