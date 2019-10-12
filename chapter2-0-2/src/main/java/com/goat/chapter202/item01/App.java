package com.goat.chapter202.item01;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Created by 64274 on 2019/8/12.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/12---20:45
 */
public class App {

	/**
	 *  doit JavaConfig.class 中的 @Configuration 这个注解 为什么不加 可以正常获取到bean呢？ 再源码中找到答案！
	 *  doit context.getBean("foo")  这里的bean名称为什么使用的是 函数名？ 再源码中找到答案！
	*/
	@Test
	public void test(){
		ApplicationContext context = new AnnotationConfigApplicationContext(JavaConfig.class);
		Person foo = (Person) context.getBean("foo");
		System.out.println(foo);
	}
}
