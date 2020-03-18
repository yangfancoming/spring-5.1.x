package com.goat.chapter105.item07.buzz02;

import com.goat.chapter105.BaseTest;
import com.goat.chapter105.item07.common.TestService;
import com.goat.chapter105.item07.common.TestService2;
import com.goat.chapter105.item07.common.TestService3;
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
	*/
	@Test
	public void getBean2(){
		TestService testService = ac.getBean(TestService.class);
		testService.printDao();
	}

	// 测试：@Autowired TestDao testDao2;  这里获取的就是 mark为2的 TestDao了
	@Test
	public void getBean22(){
		TestService2 testService2 = ac.getBean(TestService2.class);
		testService2.printDao();
	}

	/**
	 * 3）、@Qualifier("testDao")：使用@Qualifier指定需要装配的组件的id，而不是使用属性名
	 * 即使 TestService3 中是 @Autowired TestDao testDao2;
	 * 但由于 @Qualifier("testDao")
	 * 所以 获取到的是 mark为1  common包下的 TestDao
	 */
	@Test
	public void getBean222(){
		TestService3 testService3 = ac.getBean(TestService3.class);
		testService3.printDao();
	}
}
