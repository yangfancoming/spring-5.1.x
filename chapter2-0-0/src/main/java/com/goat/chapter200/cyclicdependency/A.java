package com.goat.chapter200.cyclicdependency;

/**
 * Created by Administrator on 2020/4/3.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/3---14:57
 */
public class A {

	public A() {
		new B();
	}
}
