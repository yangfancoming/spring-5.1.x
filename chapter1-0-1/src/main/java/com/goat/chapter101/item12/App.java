package com.goat.chapter101.item12;

import org.junit.Test;

/**
 * 解决item11 出现的问题
 * 依赖关系传递的三种方式：
 * 　1、通过接口传递(上述示例)
 *   2、通过构造方法传递　　
 *   3、通过Setter方法传递　　
 *
 * 通过以下三种方式的对比： 第一种最优秀
*/
public class App {

	@Test
	public void test(){
		XiaoMing xm = new XiaoMing();
		xm.Study(new CSharp());
		xm.Study(new Linux());
	}

	@Test
	public void test2(){
		XiaoMing2 xm = new XiaoMing2(new CSharp());
		xm.Study();
		XiaoMing2 xm2 = new XiaoMing2(new Linux());
		xm2.Study();
	}

	@Test
	public void test3(){
		XiaoMing3 xm = new XiaoMing3();
		xm.setBook(new CSharp());
		xm.Study();
		xm.setBook(new Linux());
		xm.Study();
	}
}
