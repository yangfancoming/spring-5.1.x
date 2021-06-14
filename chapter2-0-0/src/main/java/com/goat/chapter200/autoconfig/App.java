package com.goat.chapter200.autoconfig;


import com.goat.chapter200.base.BaseTest;
import com.goat.chapter200.base.CompactDisc;
import com.goat.chapter200.base.MediaPlayer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.support.BeanNameGenerator;

public class App extends BaseTest  {

	@Before
	public void before(){
		pre(CDPlayerConfig.class);
	}

	/**
	 * Spring 生成bean名称规则
	 * @see BeanNameGenerator#generateBeanName(org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.factory.support.BeanDefinitionRegistry)
	*/
	@Test
	public void test1(){
		// 测试 默认命名组件name
		CompactDisc beyond = (CompactDisc)ctx.getBean("beyond");
		beyond.play();

		// 测试 显示命名组件name
		CompactDisc bp = (CompactDisc)ctx.getBean("bp");
		bp.play();
	}

	/**  测试  构造函数注入*/
	@Test
	public void test3(){
		MediaPlayer ent = (MediaPlayer)ctx.getBean("CDPlayer");
		ent.insert();
	}

	/**  测试  setter方法注入 */
	@Test
	public void test4(){
		MediaPlayer ent = (MediaPlayer)ctx.getBean("boxPlayer");
		ent.insert();
	}
}
