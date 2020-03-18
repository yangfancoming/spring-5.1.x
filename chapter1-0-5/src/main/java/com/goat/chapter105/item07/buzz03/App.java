package com.goat.chapter105.item07.buzz03;

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
	 * 2）、如果找到多个相同类型的组件，再将属性的名称作为组件的id去容器中查找 applicationContext.getBean("bookDao")
	 * 由于 MyConfig中的TestDao BeanId 为testDao2
	 * 所以 获取到的是 mark为1  common包下的 TestDao
	 *
	 * 测试： 可以将 com.goat.chapter105.item07.common.TestService 中使用 testDao2  这里获取的就是 mark为2的 TestDao了
	*/
	@Test
	public void getBean2(){
		TestService testService = ac.getBean(TestService.class);
		testService.printDao();
	}
}
