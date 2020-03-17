package com.goat.chapter200.javaconfig;


import com.goat.chapter200.base.CompactDisc;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App2  {

	/**  测试 spring  */
	@Test
	public void test1(){
		ApplicationContext ctx = new AnnotationConfigApplicationContext(CDPlayerConfig.class);
		CompactDisc disc = (CompactDisc)ctx.getBean("compactDisc");
		disc.play();
	}

}
