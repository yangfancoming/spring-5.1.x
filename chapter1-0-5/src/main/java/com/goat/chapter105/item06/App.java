package com.goat.chapter105.item06;

import com.goat.chapter105.BaseTest;
import com.goat.chapter105.common.ValueTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

/**
 *  学习 @Value 注解  对应传统xml方式中 <bean>标签下的<property>标签
 *
 *  <bean id="person" class="com.goat.chapter105.model.Person"  scope="prototype">
 *      <property name="name" value="goat"/>
 * 		<property name="age" value="18"/>
 * 	</bean>
*/
public class App extends BaseTest {

	ApplicationContext ac = new AnnotationConfigApplicationContext(MyConfig.class);

	@Test
	public void getBean(){
		ValueTest valueTest = (ValueTest) ac.getBean("valueTest");
		System.out.println(valueTest);
	}

	// 通过 Environment 来查看 @PropertySource 导入的内容
	@Test
	public void getEnvironment(){
		Environment environment = ac.getEnvironment();
		String property = environment.getProperty("ValueTest.nickName");
		Assert.assertEquals("goodluck",property);
	}

}
