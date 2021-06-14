package com.goat.chapter180.item02;

/**
 * Created by 64274 on 2019/8/17.
 *
 * @ Description: 工厂方法实例化
 * @ author  山羊来了
 * @ date 2019/8/17---19:30
 */

public class DogFactory {

	// 普通函数
	public Dog newInstance(String name, int age) {
		return new Dog(name, age);
	}
}