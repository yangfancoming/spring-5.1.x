package com.goat.chapter200.autoconfig;


import com.goat.chapter200.base.BaseTest;
import com.goat.chapter200.base.CompactDisc;
import com.goat.chapter200.base.MediaPlayer;
import org.junit.Before;
import org.junit.Test;

public class App extends BaseTest  {

	@Before
	public void before(){
		pre(CDPlayerConfig.class);
	}

	/**  测试 spring 默认命名组件name */
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
