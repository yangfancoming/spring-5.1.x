package com.goat.spring.demo.annotation.item01;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/30---14:16
 */
public class MyClass {

	@MyAliasAnnotation(location = "我是 location")
	public static void one() {
	}

	@MyAliasAnnotation(value = "我是 value")
	public static void one2() {
	}
}