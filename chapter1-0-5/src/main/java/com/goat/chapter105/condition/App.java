package com.goat.chapter105.condition;

import com.goat.chapter105.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by Administrator on 2020/3/17.
 * @ Description: 注解方式 获取bean
 * @ author  山羊来了
 * @ date 2020/3/17---13:24
 */
public class App extends BaseTest {

	@Test
	public void test1(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ConditionalClassConfig1.class);
		Assert.assertTrue(ac.containsBean("zoo"));
		Assert.assertTrue(ac.containsBean("foo"));
	}

	@Test
	public void test2(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ConditionalClassConfig2.class);
		Assert.assertFalse(ac.containsBean("zoo"));
		Assert.assertFalse(ac.containsBean("foo"));
	}

	/**
	 * ***---***	 personConfig
	 * ***---***	 bill
	*/
	@Test
	public void method(){
		ApplicationContext ac = new AnnotationConfigApplicationContext(ConditionalMethodConfig.class);
		look(ac);
	}


}
