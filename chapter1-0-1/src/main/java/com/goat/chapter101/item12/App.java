package com.goat.chapter101.item12;

import org.junit.Test;



/**
 * 解决item11 出现的问题
*/
public class App {

	@Test
	public void test(){
		XiaoMing xm = new XiaoMing();
		xm.Study(new CSharp());
		xm.Study(new Linux());
	}
}
