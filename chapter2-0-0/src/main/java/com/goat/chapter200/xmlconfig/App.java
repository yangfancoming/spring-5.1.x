package com.goat.chapter200.xmlconfig;


import com.goat.chapter200.base.MediaPlayer;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

	ApplicationContext ac1 = new ClassPathXmlApplicationContext("classpath:ConstructorArgValueTest-context.xml");
	ApplicationContext ac2 = new ClassPathXmlApplicationContext("classpath:ConstructorArgValueTest-context.xml");
	ApplicationContext ac3 = new ClassPathXmlApplicationContext("classpath:ConstructorArgCollectionTest-context.xml");
	ApplicationContext ac4 = new ClassPathXmlApplicationContext("classpath:PNamespaceRefTest-context.xml");

	/**  测试 xml 配置方式  */
	@Test
	public void test1(){
		MediaPlayer disc = (MediaPlayer)ac1.getBean("cdPlayer");
		disc.insert();
	}

	/**  测试 xml 采用 C命名空间 配置方式  */
	@Test
	public void test2(){
		MediaPlayer disc = (MediaPlayer)ac2.getBean("cdPlayer");
		disc.insert();
	}

	/**  测试 xml  装配集合 */
	@Test
	public void test3(){
		MediaPlayer disc = (MediaPlayer)ac3.getBean("cdPlayer");
		disc.insert();
	}

	/**  测试 xml  装配集合 */
	@Test
	public void test4(){
		MediaPlayer disc = (MediaPlayer)ac4.getBean("cdPlayer");
		disc.insert();
	}
}
