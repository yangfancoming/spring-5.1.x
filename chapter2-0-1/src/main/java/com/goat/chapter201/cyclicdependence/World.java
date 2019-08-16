package com.goat.chapter201.cyclicdependence;

/**
 * Created by 64274 on 2019/8/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/16---13:11
 */
public class World {

	Hello hello;

	public Hello getHello() {
		return hello;
	}

	public void setHello(Hello hello) {
		this.hello = hello;
	}

	public World() {
		System.out.println("World 构造函数 执行！循环依赖测试");
	}
}
