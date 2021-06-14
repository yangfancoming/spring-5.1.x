package com.goat.chapter180.item02;

/**
 * Created by 64274 on 2019/8/17.
 *
 * @ Description: 静态工厂实例化
 * @ author  山羊来了
 * @ date 2019/8/17---19:31
 */
public class DogStaticFactory {

	// 静态工厂方法
	public static Dog newInstance(String name) {
		// 返回需要的Bean实例
		return new Dog(name);
	}
}