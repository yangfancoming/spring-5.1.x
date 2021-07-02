package com.goat.chapter201.lifecycle.item01;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 我们的任何一个Spring Bean若实现了EmbeddedValueResolverAware接口，
 * Spring容器在启动的时候就会自动给我们我们的Bean注入进来一个StringValueResolver。
 * 然后我们借用这个resolver就能处理一系列字符串的逻辑比如：占位符解释、SpEL计算等等~
 */
public class EmbeddedValueResolverAwareTestApp {

	@Test
	public void test2() {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("EmbeddedValueResolverAwareTest.xml");
		System.out.println(ac);
		ac.close();
	}
}
