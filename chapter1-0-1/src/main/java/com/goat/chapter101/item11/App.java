package com.goat.chapter101.item11;

import org.junit.Test;


/**
 * 引出问题
 * 详见 item12 解决问题
*/
public class App {

	// 三二班有个小明，想要学习C#，于是买了本《深入理解C#》进行学习。
	@Test
	public void test(){
		XiaoMing xm = new XiaoMing();
		xm.Study(new CSharp());
	}

	// 过了一段时间，小明觉得光学习一门太没有意思了。听说Linux比较好玩，于是买了本《鸟哥的私房菜Linux》。
	// 这样导致 每次学习新的数据 都要在XiaoMing类中添加对应的 Study 方法，此类依赖了书籍CSharp和Linux又是一个细节依赖类，这导致XiaoMing每读一本书都需要修改代码，这与我们的依赖倒置原则是相悖的
	@Test
	public void test1(){
		XiaoMing xm = new XiaoMing();
		xm.Study(new CSharp());
		xm.Study(new Linux());
	}
}
