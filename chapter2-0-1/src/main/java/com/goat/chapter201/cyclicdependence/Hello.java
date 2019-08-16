package com.goat.chapter201.cyclicdependence;

/**
 * Created by 64274 on 2019/8/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/16---13:11
 */
public class Hello {

	World world;

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Hello() {
		System.out.println("Hello 构造函数 执行！ 循环依赖测试");
	}
}
