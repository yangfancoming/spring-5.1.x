package com.goat.chapter105.item07.buzz05;

import com.goat.chapter105.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class App2 extends BaseTest {

	ApplicationContext ac2 = new AnnotationConfigApplicationContext(MyConfig2.class);

	/**
	 *  3.@Autowired 标注在  @Configuration配置类中的@Bean 方法上时 参数的 @Autowired 注解可以省略
	 *  方法参数的值 会默认从容器中获取
	 */
	@Test
	public void getBean3(){
		Boss3 boss = ac2.getBean(Boss3.class);
		System.out.println(boss.toString());
		Car car = ac2.getBean(Car.class);
		Assert.assertEquals(car,boss.getCar());
	}

}
