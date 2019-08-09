package com.goat.chapter101.item01;

/**
 * Created by 64274 on 2019/8/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/9---15:19
 */
public class Car {

	private Framework framework;

	public Car() {
		this.framework = new Framework();
	}

	public Framework getFramework() {
		return framework;
	}

	public void setFramework(Framework framework) {
		this.framework = framework;
	}

	public void run(){
		System.out.println("汽车生产出来了  size为：" + framework.getBottom().getTire().getSize() );
	}
}
