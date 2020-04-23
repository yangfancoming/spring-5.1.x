package com.goat.chapter201.replacedmethod;

import org.springframework.beans.factory.support.MethodReplacer;

import java.lang.reflect.Method;

/**
 * replaced-method 测试，需要实现接口 MethodReplacer
 */
public class TestMethodReplaced implements MethodReplacer {

	@Override
	public Object reimplement(Object obj, Method method, Object[] args) {
		System.out.println("替换方法，会被执行！");
		return null;
	}
}
