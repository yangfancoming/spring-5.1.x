package com.goat.chapter105.item07.buzz05;

import com.goat.chapter105.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App1 extends BaseTest {

	ApplicationContext ac = new AnnotationConfigApplicationContext(MyConfig.class);

	@Test
	public void forAll(){look(ac);}

	/**
	 * @Autowired  注解标注的位置：
	 * 1.在 setter 方法上使用
	*/
	@Test
	public void getBean1(){
		Boss boss = ac.getBean(Boss.class);
		System.out.println(boss.toString());

		// 证明boss中的car 就是从spring容器中获取的
		Car car = ac.getBean(Car.class);
		Assert.assertEquals(car,boss.getCar());
	}

	/**
	 *  @Autowired 标注在 有参构造器上 @Autowired
	 *  如果组件只有一个 有参构造器，那么这个参数的  @Autowired 可以省略
	 * 2. 默认加载容器中的组件，容器启动会调用无参构造器来创建对象，再进行初始化赋值等操作
	*/
	@Test
	public void getBean2(){
		Boss2 boss = ac.getBean(Boss2.class);
		System.out.println(boss.toString());

		Car car = ac.getBean(Car.class);
		Assert.assertEquals(car,boss.getCar());
	}

}
