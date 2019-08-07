package com.goat.chapter200.autoconfig;


import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class App {

	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

	@Before
	public void pre(){
		ctx.register(CDPlayerConfig.class);
		ctx.refresh();

		String[] str= ctx.getBeanDefinitionNames();
		Arrays.stream(str).forEach(x->System.out.println("***---***" + x));
	}

	/**  测试 spring默认命名组件name */
	@Test
	public void test1(){
		CompactDisc disc = (CompactDisc)ctx.getBean("beyond");
		disc.play();
	}

	/**  测试 显示命名组件name */
	@Test
	public void test2(){
		CompactDisc disc = (CompactDisc)ctx.getBean("bp");
		disc.play();
	}

	/**  测试  */
	@Test
	public void test3(){
		MediaPlayer ent = (MediaPlayer)ctx.getBean("CDplayer");
		ent.insert();
	}

}
