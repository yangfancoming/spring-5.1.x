package com.goat.chapter201.factorymethod;

import com.goat.chapter201.common.Person;

/**
 * Created by 64274 on 2019/8/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/16---12:24
 */
public class StaticHelloFactory {

	public static Person getHello() {
		Person hello = new Person();
		hello.setName("created by StaticHelloFactory");
		return hello;
	}
}