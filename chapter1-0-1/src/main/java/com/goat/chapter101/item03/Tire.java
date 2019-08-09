package com.goat.chapter101.item03;

/**
 * Created by 64274 on 2019/8/9.
 *
 * @ Description: 轮胎
 * @ author  山羊来了
 * @ date 2019/8/9---15:16
 */
public class Tire {

	private int size;

	public Tire() {
		this.size = 4;
	}

	/** 改成可变size 只需要增加一个带参的构造函数就可以了 不需要更改其他的依赖类*/
	public Tire(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
}
