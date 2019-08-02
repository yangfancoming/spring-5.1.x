package com.goat.chapter200.item01;


import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

	/**  测试 */
	@Test
	public void test2(){
		// 用我们的配置文件来启动一个 ApplicationContext
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:CNamespaceReferenceTest-context.xml");
		// 从 context 中取出我们的 Bean，而不是用 new CDPlayer() 这种方式
		MediaPlayer player = context.getBean(MediaPlayer.class);
		player.play();
	}



	/**  测试 */
	@Test
	public void test1(){
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:BeanAll.xml");
		MediaPlayer player = context.getBean(MediaPlayer.class);
		player.play();
	}
}
