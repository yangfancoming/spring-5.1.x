package com.goat.chapter200.javaconfig;


import com.goat.chapter200.base.BaseTest;
import com.goat.chapter200.base.CompactDisc;
import com.goat.chapter200.base.MediaPlayer;
import org.junit.Before;
import org.junit.Test;

public class App extends BaseTest {

	/**  测试 spring 默认命名组件name */
	@Before
	public void before(){
		pre(CDPlayerConfig.class);
	}

	/**  测试 spring  */
	@Test
	public void test1(){
		CompactDisc disc = (CompactDisc)ctx.getBean("compactDisc");
		disc.play();
	}

	/**  测试 spring  */
	@Test
	public void test2(){
		MediaPlayer disc = (MediaPlayer)ctx.getBean("cdPlayer");
		disc.insert();
	}
}
