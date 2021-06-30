package com.goat.chapter201.factoryBean.item02;

/**
 * Created by 64274 on 2019/8/17.
 *
 * @ Description: 椅子
 * @ author  山羊来了
 * @ date 2019/8/17---19:48
 */
public class Chair implements Furniture{

	@Override
	public void sayHello() {
		System.out.println("我是一把椅子。");
	}
}
